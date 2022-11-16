package com.gaojy.rice.controller;

import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.controller.config.ControllerConfig;
import com.gaojy.rice.controller.handler.HttpRouter;
import com.gaojy.rice.controller.longpolling.PullTaskRequestHoldService;
import com.gaojy.rice.controller.maintain.SchedulerManager;
import com.gaojy.rice.controller.processor.SchedulerManagerProcessor;
import com.gaojy.rice.controller.processor.TaskAccessProcessor;
import com.gaojy.rice.controller.replicator.ControllerDataService;
import com.gaojy.rice.controller.replicator.ControllerDataServiceImpl;
import com.gaojy.rice.controller.replicator.LeaderStateListener;
import com.gaojy.rice.controller.replicator.RiceReplicatorManager;
import com.gaojy.rice.http.api.HttpBinder;
import com.gaojy.rice.http.api.HttpServer;
import com.gaojy.rice.remote.ChannelEventListener;
import com.gaojy.rice.remote.transport.TransfServerConfig;
import com.gaojy.rice.remote.transport.TransportServer;
import com.gaojy.rice.repository.api.Repository;
import io.netty.channel.Channel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName RiceController.java
 * @Description
 * @createTime 2022/01/08 10:39:00
 */
public class RiceController implements LeaderStateListener, ChannelEventListener {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.CONTROLLER_LOGGER_NAME);

    private final Repository repository = ExtensionLoader.getExtensionLoader(Repository.class)
        .getExtension(System.getProperty(Repository.REPOSITORY_TYPE_KEY, "mysql"));

    private final HttpBinder httpBinder = ExtensionLoader.getExtensionLoader(HttpBinder.class).getExtension("jetty");

    private final ControllerConfig controllerConfig;

    private final RiceReplicatorManager replicatorManager;

    private TransfServerConfig transfServerConfig;

    private final TransportServer remotingServer;

    private HttpServer httpServer;

    private ExecutorService taskAccessExecutor;

    private ExecutorService schedulerManagerExecutor;

    private SchedulerManagerProcessor schedulerManagerProcessor;

    private PullTaskRequestHoldService pullTaskRequestHoldService;

    private ControllerDataService controllerDataService;

    public RiceController(ControllerConfig controllerConfig, TransfServerConfig transfServerConfig) {
        this.controllerConfig = controllerConfig;
        this.transfServerConfig = transfServerConfig;
        // 替换业务端口
        this.transfServerConfig.setListenPort(controllerConfig.getControllerPort());

        this.remotingServer = new TransportServer(this.transfServerConfig, this);
        this.replicatorManager = new RiceReplicatorManager(this.controllerConfig, this);
        controllerDataService = new ControllerDataServiceImpl(this.replicatorManager.getServer());

        this.taskAccessExecutor = Executors.newFixedThreadPool(this.controllerConfig.getTaskAccessThreadPoolNums(),
            new RiceThreadFactory("TaskAccessThread_"));
        this.schedulerManagerExecutor = Executors.newFixedThreadPool(this.controllerConfig.getSchedulerManagerThreadPoolNums(),
            new RiceThreadFactory("SchedulerManagerThread_"));
        this.pullTaskRequestHoldService = new PullTaskRequestHoldService(this);
        // 注册业务处理器
        this.doProcessorRegister();
        this.doHttpHandlers();
    }

    @Override
    public void onLeaderStart(long leaderTerm) {
        // 长轮询数据处理
        pullTaskRequestHoldService.start();

    }

    @Override
    public void onLeaderStop(long leaderTerm) {
        pullTaskRequestHoldService.shutdown();
    }

    public void start() {
        // 启动选举
        this.replicatorManager.start();
        // 启动业务服务端
        this.remotingServer.start();
        // 启动管理员控制台
        httpServer = this.httpBinder.bind(this.controllerConfig.getManagePort());
    }

    public void shutdown() {
        this.replicatorManager.stop();
        this.remotingServer.shutdown();
        if (httpServer != null && !httpServer.isClosed()) {
            httpServer.close();
        }
        // 关闭数据源
        repository.close();
        // 关闭网络处理线程池
        taskAccessExecutor.shutdown();
        schedulerManagerExecutor.shutdown();

        if (!pullTaskRequestHoldService.isStopped()) {
            pullTaskRequestHoldService.shutdown();
        }

    }

    private void doProcessorRegister() {
        // 处理器注册请求把处理器先保存数据库，然后获取对应的几个调度器，依次通过控制器通知调度器。 失败，则由processor重试
        remotingServer.registerProcessor(RequestCode.REGISTER_PROCESSOR, new TaskAccessProcessor(this), this.taskAccessExecutor);

        schedulerManagerProcessor = new SchedulerManagerProcessor(this);

        // 调度器注册处理
        remotingServer.registerProcessor(RequestCode.SCHEDULER_REGISTER, schedulerManagerProcessor, this.schedulerManagerExecutor);

        // 调度器心跳处理 调度器上报状态处理
        remotingServer.registerProcessor(RequestCode.SCHEDULER_HEART_BEAT, schedulerManagerProcessor, this.schedulerManagerExecutor);

        //  调度器拉取任务change处理
        remotingServer.registerProcessor(RequestCode.SCHEDULER_PULL_TASK, schedulerManagerProcessor, this.schedulerManagerExecutor);

    }

    // http handler处理器注册
    private void doHttpHandlers() {
        HttpRouter.addHandlers(httpBinder);
        // httpBinder.addHttpHandler();

    }

    /**
     * master需要处理任务分配
     */

    public Boolean isLongPollingEnable() {
        return true;
    }

    public Long getShortPollingTimeMills() {
        return 1000L;
    }

    public Repository getRepository() {
        return this.repository;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        // 首先看一下调度器列表中是否存在该连接，如果连接确是已经失效 则删除该通道信息

        if (SchedulerManager.getManager().is_scheduler(remoteAddr)) {
            // 删除该scheulerserver
            SchedulerManager.getManager().closeScheduler(remoteAddr);

            if (isMaster()) {
                // 并且是leader 那么就触发任务分配
                log.info("Detects that the scheduler:{} is offline and notifies other schedulers by broadcasting", remoteAddr);
                schedulerManagerProcessor.crashDownScheduler(remoteAddr);
            }
        }

    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        this.onChannelClose(remoteAddr, channel);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        this.onChannelClose(remoteAddr, channel);
    }

    public Boolean isMaster() {
        return replicatorManager.isLeader();
    }

    public ExecutorService getSchedulerManagerExecutor() {
        return schedulerManagerExecutor;
    }

    public PullTaskRequestHoldService getPullTaskRequestHoldService() {
        return pullTaskRequestHoldService;
    }

    public ControllerDataService getControllerDataService() {
        return controllerDataService;
    }
}
