package com.gaojy.rice.processor.api;

import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.balance.Balance;
import com.gaojy.rice.common.balance.BalanceFactory;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.exception.RegisterProcessorException;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.protocol.body.processor.ExportTaskRequestBody;
import com.gaojy.rice.common.protocol.body.processor.ExportTaskResponseBody;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskRequestHeader;
import com.gaojy.rice.common.utils.RiceBanner;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.processor.api.common.ElectionClient;
import com.gaojy.rice.processor.api.config.ProcessorConfig;
import com.gaojy.rice.processor.api.invoker.TaskInvoker;
import com.gaojy.rice.processor.api.log.RiceClientLogger;
import com.gaojy.rice.processor.api.register.DefaultTaskScheduleProcessor;
import com.gaojy.rice.remote.ChannelEventListener;
import com.gaojy.rice.remote.protocol.RemotingSysResponseCode;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.TransportClient;
import com.gaojy.rice.remote.transport.TransportServer;

import io.netty.channel.Channel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;

/**
 * @author gaojy
 * @ClassName RiceProcessorManager.java
 * @Description RICE 处理器管理类
 * @createTime 2022/01/04 20:56:00
 */
public class RiceProcessorManager implements ChannelEventListener {
    private final Logger log = RiceClientLogger.getLog();
    private static RiceProcessorManager manager = null;
    private static Object monitorObj = new Object();
    private final ProcessorConfig config;
    private final TransportServer server;
    private final TransportClient client;
    // 处理业务线程池
    private final ExecutorService remotingExecutor;

    private final ElectionClient electionClient;

    private RiceProcessorManager(ProcessorConfig config) {
        this.config = config;
        server = new TransportServer(config.getTransfServerConfig(), this);
        client = new TransportClient(config.getTransfClientConfig());
        remotingExecutor =
            Executors.newFixedThreadPool(config.getTransfServerConfig().getServerWorkerThreads(),
                new RiceThreadFactory("RemotingExecutorThread_"));
        electionClient = new ElectionClient(config);
    }

    public static RiceProcessorManager getManager() {
        if (manager == null) {
            synchronized (monitorObj) {
                if (manager == null) {
                    ProcessorConfig config = new ProcessorConfig();
                    manager = new RiceProcessorManager(config);
                    return manager;
                }
            }
        }
        return manager;
    }

    public void export() {
        // 收集所有的task 并缓存起来
        TaskInvoker.init(config);

        // 注册业务处理器  启动监听
        register();
        this.server.start();

        this.client.start();
        this.electionClient.close();

        RiceRemoteContext context = buildRegisterRequest();
        doRegister(context);
        // 完成注册之后 close client
        this.client.shutdown();
        // 如果控制器成功返回 打印banner
        RiceBanner.show(7);
    }

    public void close() {
        this.server.shutdown();
    }

    private void register() {
        server.registerProcessor(RequestCode.INVOKE_PROCESSOR,
            new DefaultTaskScheduleProcessor(this), this.remotingExecutor);
        server.registerProcessor(RequestCode.SCHEDULER_HEART_BEAT,
            new DefaultTaskScheduleProcessor(this), this.remotingExecutor);

    }

    private RiceRemoteContext buildRegisterRequest() {
        ExportTaskRequestHeader header = new ExportTaskRequestHeader();
        header.setListenPort(server.getPort());
        header.setAppId(config.getAppId());
        header.setNetAddress(server.getServerAddress());
        final ExportTaskRequestBody body = new ExportTaskRequestBody();
        TaskInvoker.getTaskInvokerMap().values().stream().forEach(invoker -> {
            body.addTask(invoker.getTaskDetailData());
        });
        RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(RequestCode.REGISTER_PROCESSOR, header);
        requestCommand.setBody(body.encode());
        return requestCommand;
    }

    /**
     * 向控制器注册  并且最多重试5次
     *
     * @param riceRemoteContext
     */
    void doRegister(RiceRemoteContext riceRemoteContext) {
//        Balance balance = BalanceFactory.getBalance(this.config.getBalancePolicy());
        String addr = null;
        int retryConnCount = 5;
        while (retryConnCount >= 0) {
            try {
                addr = this.electionClient.getMasterController();
//                addr = balance.select(config.getControllerServerList());
                RiceRemoteContext registerResult = client.invokeSync(addr, riceRemoteContext, 3 * 1000);
                switch (registerResult.getCode()) {
                    case RemotingSysResponseCode.SUCCESS: {
                        ExportTaskResponseBody body = ExportTaskResponseBody.decode(registerResult.getBody(), ExportTaskResponseBody.class);
                        if (!body.getTaskSchedulerInfo().isEmpty()) {
                            log.info("app:" + config.getAppId() + " register to controller [" + addr + "] successfully");
                            body.getTaskSchedulerInfo().forEach((k, v) -> {
                                if (StringUtil.isNotEmpty(v)) {
                                    log.info("taskcode:" + k + " will scheduled by server " + v);
                                } else {
                                    log.info("taskcode:" + k + " have not been assigned yet ");
                                }
                            });
                        }
                        return;

                    }
                    default:
                        break;

                }
            } catch (RemotingConnectException | RemotingTimeoutException
                | RemotingSendRequestException | InterruptedException | TimeoutException e) {
                log.error("Register to controller Exception, controller address is " + addr);
            }
            retryConnCount--;
        }

        throw new RegisterProcessorException("Register to controller Exception,please check controller servers");
    }

    public TransportClient getClient() {
        return client;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        log.info("New Scheduler join to execute,remoteAddr=" + remoteAddr);
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        log.warn("Scheduler:" + remoteAddr + " ,no longer to execute tasks on this processor ");
        channel.close();
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        log.error("Scheduler exception,remoteAddr:" + remoteAddr + " ,no longer to execute tasks on this processor ");
        channel.close();
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        log.error("The dispatcher has not communicated for a long time and is idle abnormally");
        channel.close();
    }
}
