package com.gaojy.rice.dispatcher;

import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.entity.ProcessorServerInfo;
import com.gaojy.rice.common.exception.DispatcherException;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.common.protocol.body.scheduler.SchedulerHeartBeatBody;
import com.gaojy.rice.common.utils.RiceBanner;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.dispatcher.common.DispatcherAPIWrapper;
import com.gaojy.rice.dispatcher.common.ElectionClient;
import com.gaojy.rice.dispatcher.common.HardwareHelper;
import com.gaojy.rice.dispatcher.config.DispatcherConfig;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;
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

    // 这个参数 会传给主控制器  来决定是否通知所有调度器集群是否启动任务重分配
    private volatile boolean isFirstRegisterToController = true;
    // 实现远程管理  远程关机
    private JMXConnectorServer jmxConnServer;

    private final TransfClientConfig transfClientConfig;

    private final DispatcherConfig dispatcherConfig;

    // 选举客户端
    private final ElectionClient electionClient;

    // 业务rpc client
    private final TransportClient transportClient;

    private final DispatcherAPIWrapper apiWrapper;

    // 任务调度管理器
    private final TaskScheduleManager scheduleManager;

    // 心跳线程池
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

    /**
     * @description TODO : 暂时不引入  JMX 管理
     */
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
        //startJMXManagement();
        transportClient.registerProcessor(RequestCode.CONTROLLER_TASK_REBALANCE,
            new TaskRebalanceProcessor(scheduleManager), null);
        transportClient.registerProcessor(RequestCode.CONTROLLER_TASK_CREATE,
            new TaskCreateProcessor(scheduleManager), null);
        transportClient.start();

        scheduleManager.startup();

        // 向主控制器发送心跳
        executorService.scheduleAtFixedRate(() -> {
            try {
                final String address = electionClient.getMasterController();
                // 每一次跟主控制器的心跳都会上传有效的处理器信息  还有系统信息
                Set<String> processores = scheduleManager.getValidProcessorAddress();
                SchedulerHeartBeatBody body = new SchedulerHeartBeatBody();
                processores.forEach(p -> {
                    SchedulerHeartBeatBody.ProcessorDetail pd = new SchedulerHeartBeatBody.ProcessorDetail();
                    pd.setAddress(p);
                    pd.setLatestActiveTime(scheduleManager.procesorLatestHeartBeat(p));
                    body.addProcessorDetail(pd);
                });
                body.setTaskCodes(scheduleManager.getManageTask());
                body.setMenRate(HardwareHelper.getMemoryRatio());
                body.setCPURate(HardwareHelper.getProcessCpuLoad());
                apiWrapper.heartBeatToController(address, body);
            } catch (InterruptedException | TimeoutException | RemotingConnectException
                | RemotingSendRequestException | RemotingTimeoutException | RemotingTooMuchRequestException e) {
                log.error("heartBeatToController occur exception" + e);
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
//        if (jmxConnServer != null) {
//            jmxConnServer.stop();
//        }
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

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        this.handleProcessorClose(remoteAddr);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        this.handleProcessorClose(remoteAddr);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        this.handleProcessorClose(remoteAddr);
    }

    private void handleControllerConnect(String remoteAddr, Channel channel) {
        // 如果是控制器的连接上  发register
        // 一旦主控制器宕机  心跳调度会不断尝试重新连接
        String addressStr = "";
        try {
            addressStr = electionClient.getMasterController();

            if (StringUtil.isNotEmpty(addressStr) && addressStr.equals(remoteAddr)) {
                log.info("Connect to the controller for the {} first time and send a registration request. Address:{}",
                    isFirstRegisterToController ? "" : "not", addressStr);

                Boolean ret = apiWrapper.registerScheduler(addressStr, isFirstRegisterToController);
                if (!ret) {  // 注册失败  抛出异常
                    log.error("The scheduler failed to register with the controller. Please check. Address:{}", addressStr);
                    throw new DispatcherException("The scheduler failed to register with the controller. Address:" + addressStr);
                }
                isFirstRegisterToController = false;

            } else {
                log.info("The connected remote address is not in the controller list. Address:{}", addressStr);
            }
        } catch (InterruptedException | TimeoutException | RemotingConnectException |
            RemotingSendRequestException | RemotingTimeoutException e) {
            log.error("There was an error registering the controller and the channel will be closed，Address:{}", addressStr, e);
            TransfUtil.closeChannel(channel);
            throw new DispatcherException("The scheduler failed to register with the controller. Address:" + addressStr, e);
        }

    }

    private void handleProcessorClose(String remoteAddr) {
        // 如果是处理器的连接断开 ，则隔离该处理器
        String address = remoteAddr.split(":")[0];
        Integer port = Integer.parseInt(remoteAddr.split(":")[1]);
        // 根据ip和端口查询确认
        List<ProcessorServerInfo> servers = scheduleManager.getRepository().getProcessorServerInfoDao().getInfosByServer(null, address, port);

        if (servers != null && servers.size()>0) { // 断开的是处理器连接 update

            scheduleManager.isolationProcessorForAllTasks(remoteAddr);

            // 修改数据库
            servers.stream().forEach(server->{
                server.setStatus(0);
            });
            scheduleManager.getRepository().getProcessorServerInfoDao().batchCreateOrUpdateInfo(servers);
        } else {
            log.info("disconnection detected, address:{}", remoteAddr);
        }

    }
}
