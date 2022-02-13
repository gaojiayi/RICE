package com.gaojy.rice.dispatcher;

import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.exception.DispatcherException;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.common.utils.RiceBanner;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.dispatcher.common.DispatcherAPIWrapper;
import com.gaojy.rice.dispatcher.common.ElectionClient;
import com.gaojy.rice.dispatcher.config.DispatcherConfig;
import com.gaojy.rice.dispatcher.longpolling.PullTaskService;
import com.gaojy.rice.dispatcher.processor.TaskCreateProcessor;
import com.gaojy.rice.dispatcher.processor.TaskRebalanceProcessor;
import com.gaojy.rice.dispatcher.scheduler.TaskScheduleManager;
import com.gaojy.rice.remote.ChannelEventListener;
import com.gaojy.rice.remote.common.TransfUtil;
import com.gaojy.rice.remote.transport.TransfClientConfig;
import com.gaojy.rice.remote.transport.TransportClient;
import io.netty.channel.Channel;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RICE  任务分发调度器
 */
public class RiceDispatchScheduler implements RiceDispatchSchedulerMBean, ChannelEventListener {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.DISPATCHER_LOGGER_NAME);

    private JMXConnectorServer jmxConnServer;

    private final TransfClientConfig transfClientConfig;

    private final DispatcherConfig dispatcherConfig;

    private final ElectionClient electionClient;

    private final TransportClient transportClient;

    private final DispatcherAPIWrapper apiWrapper;

    private final TaskScheduleManager scheduleManager;

    final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3, new RiceThreadFactory("heartBeatToControllerThread"));

    public RiceDispatchScheduler(TransfClientConfig transfClientConfig, DispatcherConfig dispatcherConfig) {
        this.transfClientConfig = transfClientConfig;
        this.dispatcherConfig = dispatcherConfig;
        electionClient = new ElectionClient(this);
        transportClient = new TransportClient(this.transfClientConfig, this);
        apiWrapper = new DispatcherAPIWrapper(this);
        scheduleManager = new TaskScheduleManager(apiWrapper);
        //pullTaskService = new PullTaskService(this);

    }

    // JMX 管理
    public void startJMXManagement() throws Exception {
        int rmiPort = 1099;

        Registry registry = LocateRegistry.createRegistry(rmiPort);
        // Get the Platform MBean Server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // Construct the ObjectName for the Hello MBean we will register
        ObjectName mbeanName = new ObjectName("com.gaojy.rice.dispatcher:type=scheduler");

        // Register the Hello World MBean
        mbs.registerMBean(this, mbeanName);

        String url = "service:jmx:rmi://localhost:1010/jndi/rmi://localhost:" + rmiPort + "/jmxrmi";

        JMXServiceURL jmxUrl = new JMXServiceURL(url);
        jmxConnServer = JMXConnectorServerFactory.newJMXConnectorServer(jmxUrl, null, mbs);

        jmxConnServer.start();
    }

    public void start() throws Exception {
        startJMXManagement();
        transportClient.registerProcessor(RequestCode.CONTROLLER_TASK_REBALANCE,
            new TaskRebalanceProcessor(scheduleManager), null);
        transportClient.registerProcessor(RequestCode.CONTROLLER_TASK_CREATE,
            new TaskCreateProcessor(scheduleManager), null);
        transportClient.start();
        // 长轮询
        //pullTaskService.start();

        scheduleManager.startup();

        // 发送心跳
        executorService.scheduleAtFixedRate(() -> {
            try {
                apiWrapper.heartBeatToController(dispatcherConfig.getAllControllerAddressStr());
            } catch (InterruptedException | TimeoutException | RemotingConnectException
                | RemotingSendRequestException | RemotingTimeoutException | RemotingTooMuchRequestException e) {
                log.error("heartBeatToController occur exception"+e);
            }
        }, 1000, 1000 * 3, TimeUnit.MILLISECONDS);

        // 打印banner
        RiceBanner.show(7);
    }

    public void shutdown() throws Exception {

        electionClient.close();
        executorService.shutdown();
        scheduleManager.shutdown();

        if (this.transportClient != null) {
            transportClient.shutdown();
        }
        if (jmxConnServer != null) {
            jmxConnServer.stop();
        }
    }

    public DispatcherConfig getDispatcherConfig() {
        return dispatcherConfig;
    }

    public ElectionClient getElectionClient() {
        return electionClient;
    }

    public TransportClient getTransportClient() {
        return transportClient;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        this.handleControllerConnect(remoteAddr, channel);
    }

    @Override public void onChannelClose(String remoteAddr, Channel channel) {
        this.handleProcessorClose(remoteAddr);
    }

    @Override public void onChannelException(String remoteAddr, Channel channel) {
        this.handleProcessorClose(remoteAddr);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        this.handleProcessorClose(remoteAddr);
    }

    private void handleControllerConnect(String remoteAddr, Channel channel) {
        // 如果是控制器的连接上  发register
        // 发送注册
        String addressStr = Arrays.stream(dispatcherConfig.getAllControllerAddressStr().split(",")).filter(address -> {
            return address.contains(remoteAddr);
        }).findFirst().get();
        if (StringUtil.isNotEmpty(addressStr)) {
            log.info("Connect to the controller for the first time and send a registration request. Address:{}", addressStr);
            try {
                Boolean ret = apiWrapper.registerScheduler(addressStr);
                if (!ret) {
                    log.error("The scheduler failed to register with the controller. Please check. Address:{}", addressStr);
                    throw new DispatcherException("The scheduler failed to register with the controller. Address:" + addressStr);
                }
            } catch (InterruptedException | TimeoutException | RemotingConnectException |
                RemotingSendRequestException | RemotingTimeoutException e) {
                log.error("There was an error registering the controller and the channel will be closed，Address:{}", addressStr);
                TransfUtil.closeChannel(channel);
            }
        } else {
            log.info("The connected remote address is not in the controller list. Address:{}", addressStr);
        }
    }

    private void handleProcessorClose(String remoteAddr) {
        // 如果是处理器的连接断开 ，则隔离该处理器
        String addressStr = Arrays.stream(dispatcherConfig.getAllControllerAddressStr().split(",")).filter(address -> {
            return address.contains(remoteAddr);
        }).findFirst().get();
        if (StringUtil.isEmpty(addressStr)) { // 断开的是处理器连接
            scheduleManager.isolationProcessorForAllTasks(remoteAddr);
        } else {
            log.info("Controller disconnection detected, address:{}", addressStr);
        }

    }
}
