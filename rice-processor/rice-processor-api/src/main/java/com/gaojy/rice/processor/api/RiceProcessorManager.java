package com.gaojy.rice.processor.api;

import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.balance.Balance;
import com.gaojy.rice.common.balance.RoundRobinBalance;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.exception.RegisterProcessorException;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.protocol.body.processor.ExportTaskRequestBody;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskRequestHeader;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskResponseHeader;
import com.gaojy.rice.common.utils.RiceBanner;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.processor.api.config.ProcessorConfig;
import com.gaojy.rice.processor.api.invoker.TaskInvoker;
import com.gaojy.rice.processor.api.log.RiceClientLogger;
import com.gaojy.rice.processor.api.register.DefaultTaskScheduleProcessor;
import com.gaojy.rice.remote.protocol.RemotingSysResponseCode;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.TransportClient;
import com.gaojy.rice.remote.transport.TransportServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;

/**
 * @author gaojy
 * @ClassName RiceProcessorManager.java
 * @Description RICE 处理器管理类
 * @createTime 2022/01/04 20:56:00
 */
public class RiceProcessorManager {
    Logger log = RiceClientLogger.getLog();
    private static RiceProcessorManager manager = null;
    private static Object monitorObj = new Object();
    private final ProcessorConfig config;
    private final TransportServer server;
    private final TransportClient client;
    // 处理业务线程池
    private final ExecutorService remotingExecutor;

    private RiceProcessorManager(ProcessorConfig config) {
        this.config = config;
        server = new TransportServer(config.getTransfServerConfig());
        client = new TransportClient(config.getTransfClientConfig());
        remotingExecutor =
                Executors.newFixedThreadPool(config.getTransfServerConfig().getServerWorkerThreads(),
                        new RiceThreadFactory("RemotingExecutorThread_"));

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

        // 创建网络事件监听器
        // TODO:

        RiceRemoteContext context = buildRegisterRequest();
        doRegister(context);

        // 如果控制器成功返回 打印banner
        RiceBanner.show(7);
    }

    public void destory() {

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

    void doRegister(RiceRemoteContext riceRemoteContext) {
        Balance balance = new RoundRobinBalance<>(config.getControllerServerList());
        String addr = null;
        int retryConnCount = 5;
        while (retryConnCount >= 0) {
            try {
                addr = balance.select();
                RiceRemoteContext registerResult = client.invokeSync(addr, riceRemoteContext, 3 * 1000);
                switch (registerResult.getCode()) {
                    case RemotingSysResponseCode.SUCCESS: {
                        ExportTaskResponseHeader header = (ExportTaskResponseHeader) registerResult.decodeCommandCustomHeader(ExportTaskResponseHeader.class);
                        if (!header.getTaskSchedulerInfo().isEmpty()) {
                            header.getTaskSchedulerInfo().forEach((k, v) -> {
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
                    | RemotingSendRequestException | InterruptedException | RemotingCommandException e) {
                log.error("Register to controller Exception, controller address is " + addr);
            }
            retryConnCount--;
        }

        throw new RegisterProcessorException("Register to controller Exception");
    }

}
