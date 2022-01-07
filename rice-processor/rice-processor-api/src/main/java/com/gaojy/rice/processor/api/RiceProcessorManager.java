package com.gaojy.rice.processor.api;

import com.gaojy.rice.common.RiceThreadFactory;
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

    private RiceProcessorManager() {
        this(new ProcessorConfig());

    }

    public RiceProcessorManager(ProcessorConfig config) {
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
                    manager = new RiceProcessorManager();
                    return manager;
                }
            }
        }
        return manager;
    }

    public void export() {
        // 收集所有的task 并缓存起来

        //  校验task 有没有重复的task

        // 注册业务处理器  启动监听
        register();
        this.server.start();

        // 创建网络事件监听器
        // TODO:

        // 将所有的task包装成请求对象  并暴露给控制器
        Exception exception = null;
        try {
            RiceRemoteContext context = buildRegisterRequest();
            doRegister(context);
        } catch (RemotingConnectException e) {
            e.printStackTrace();
        } catch (RemotingSendRequestException e) {
            e.printStackTrace();
        } catch (RemotingTimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingCommandException e) {
            e.printStackTrace();
        } catch (RegisterProcessorException e) {
            e.printStackTrace();
        } finally {
            // 处理器刚启动的时候 会上报task信息，使用短连接进行注册
            client.shutdown();
        }

        // 如果返回失败  则 destory 并 抛出异常
        if (exception != null) {
            throw new RuntimeException(exception);
        }
        // 如果控制器成功返回 打印banner
        RiceBanner.show(7);
    }

    public void destory() {

    }

    private void register() {
        server.registerProcessor(RequestCode.INVOKE_PROCESSOR,
            new DefaultTaskScheduleProcessor(this), this.remotingExecutor);

    }

    private RiceRemoteContext buildRegisterRequest() {
        ExportTaskRequestHeader header = new ExportTaskRequestHeader();
        header.setListenPort(server.getPort());
        header.setAppId(config.getAppId());
        header.setNetAddress(server.getServerAddress());
        final ExportTaskRequestBody body = new ExportTaskRequestBody();
        config.getProcessorMap().keySet().stream().forEach(task -> {
            body.addTask(task);
        });
        RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(RequestCode.REGISTER_PROCESSOR, header);
        requestCommand.setBody(body.encode());
        return requestCommand;
    }

    void doRegister(RiceRemoteContext riceRemoteContext) throws RemotingConnectException,
        RemotingSendRequestException, RemotingTimeoutException, InterruptedException,
        RemotingCommandException, RegisterProcessorException {
        String addr = config.getMainControllerAddress() + ":" + config.getCollectorPort();
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
        throw new RegisterProcessorException("Register to controller Exception, controller address is " + addr);
    }

    public static void main(String[] args) {
        RiceProcessorManager.getManager().export();
    }
}
