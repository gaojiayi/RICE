package com.gaojy.rice.controller;

import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.controller.config.ControllerConfig;
import com.gaojy.rice.controller.election.LeaderStateListener;
import com.gaojy.rice.controller.election.RiceElectionManager;
import com.gaojy.rice.controller.longpolling.PullTaskRequestHoldService;
import com.gaojy.rice.controller.maintain.SchedulerManager;
import com.gaojy.rice.controller.processor.SchedulerManagerProcessor;
import com.gaojy.rice.controller.processor.TaskAccessProcessor;
import com.gaojy.rice.http.api.HttpBinder;
import com.gaojy.rice.http.api.HttpServer;
import com.gaojy.rice.remote.ChannelEventListener;
import com.gaojy.rice.remote.transport.TransfServerConfig;
import com.gaojy.rice.remote.transport.TransportServer;
import com.gaojy.rice.repository.api.Repository;
import io.netty.channel.Channel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName RiceController.java
 * @Description TODO
 * @createTime 2022/01/08 10:39:00
 */
public class RiceController implements LeaderStateListener, ChannelEventListener {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.CONTROLLER_LOGGER_NAME);

    private final Repository repository = ExtensionLoader.getExtensionLoader(Repository.class).getExtension("mysql");

    private final HttpBinder httpBinder = ExtensionLoader.getExtensionLoader(HttpBinder.class).getExtension("jetty");

    private final ControllerConfig controllerConfig;

    private final RiceElectionManager riceElectionManager;

    private TransfServerConfig transfServerConfig;

    private final TransportServer remotingServer;

    private HttpServer httpServer;

    private ExecutorService taskAccessExecutor;

    private ExecutorService schedulerManagerExecutor;

    private SchedulerManagerProcessor schedulerManagerProcessor;

    private PullTaskRequestHoldService pullTaskRequestHoldService;

    public RiceController(ControllerConfig controllerConfig, TransfServerConfig transfServerConfig) {
        this.controllerConfig = controllerConfig;
        this.transfServerConfig = transfServerConfig;
        this.remotingServer = new TransportServer(this.transfServerConfig, this);
        this.riceElectionManager = new RiceElectionManager(this.controllerConfig, this);

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
        this.riceElectionManager.start();
        // 启动业务服务端
        this.remotingServer.start();
        // 启动管理员控制台
        httpServer = this.httpBinder.bind(this.controllerConfig.getController_console_port());
    }

    public void shutdown() {
        this.riceElectionManager.stopElection();
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
        //对于处理器和调度器，都由业务请求来保存channel

        // 处理器注册上来以后，找到器处理器处理的所有task的调度服务器，对这个服务器做处理器注册。

    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {

        if (SchedulerManager.getManager().is_scheduler(remoteAddr)) {
            // 删除该scheulerserver
            SchedulerManager.getManager().closeScheduler(remoteAddr);

            if (this.riceElectionManager.isLeader()) {
                // 并且是leader 那么就触发任务分配
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
        return riceElectionManager.isLeader();
    }

    public ExecutorService getSchedulerManagerExecutor() {
        return schedulerManagerExecutor;
    }

    public PullTaskRequestHoldService getPullTaskRequestHoldService() {
        return pullTaskRequestHoldService;
    }
}
