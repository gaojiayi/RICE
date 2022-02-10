package com.gaojy.rice.dispatcher;

import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.common.utils.RiceBanner;
import com.gaojy.rice.dispatcher.common.DispatcherAPIWrapper;
import com.gaojy.rice.dispatcher.common.ElectionClient;
import com.gaojy.rice.dispatcher.config.DispatcherConfig;
import com.gaojy.rice.dispatcher.longpolling.PullTaskService;
import com.gaojy.rice.remote.transport.TransfClientConfig;
import com.gaojy.rice.remote.transport.TransportClient;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

/**
 * RICE  任务分发调度器
 */
public class RiceDispatchScheduler implements RiceDispatchSchedulerMBean {
    JMXConnectorServer jmxConnServer;
    private final TransfClientConfig transfClientConfig;

    private final DispatcherConfig dispatcherConfig;

    private final ElectionClient electionClient;

    private final TransportClient transportClient;

    private final DispatcherAPIWrapper apiWrapper;

    private final PullTaskService pullTaskService;

    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public RiceDispatchScheduler(TransfClientConfig transfClientConfig, DispatcherConfig dispatcherConfig) {
        this.transfClientConfig = transfClientConfig;
        this.dispatcherConfig = dispatcherConfig;
        electionClient = new ElectionClient(this);
        transportClient = new TransportClient(this.transfClientConfig);
        apiWrapper = new DispatcherAPIWrapper(this);
        pullTaskService = new PullTaskService(apiWrapper);
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
        transportClient.start();
        // 长轮询
        pullTaskService.start();
        // 发送注册
        apiWrapper.registerScheduler();
        // 发送心跳
        executorService.scheduleAtFixedRate(() -> {
            try {
                apiWrapper.heartBeatToController();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (RemotingConnectException e) {
                e.printStackTrace();
            } catch (RemotingSendRequestException e) {
                e.printStackTrace();
            } catch (RemotingTimeoutException e) {
                e.printStackTrace();
            } catch (RemotingTooMuchRequestException e) {
                e.printStackTrace();
            }
        }, 1000, 1000 * 3, TimeUnit.MILLISECONDS);

        // 打印banner
        RiceBanner.show(7);
    }

    public void shutdown() throws Exception {
        if (jmxConnServer != null) {
            jmxConnServer.stop();
        }
        if (this.transportClient != null) {
            transportClient.shutdown();
        }
        electionClient.close();
        executorService.shutdown();
    }

    // 发起长轮询获取任务

    // 发送注册()

    // 任务调用分发

    // 各种任务类型的初始化  时间轮算法

    // 向所有controller发送心跳

    // 上报processor状态

    // 处理processor 上线


    public DispatcherConfig getDispatcherConfig() {
        return dispatcherConfig;
    }

    public ElectionClient getElectionClient() {
        return electionClient;
    }

    public TransportClient getTransportClient() {
        return transportClient;
    }
}
