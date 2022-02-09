package com.gaojy.rice.dispatcher;

import com.gaojy.rice.dispatcher.common.ElectionClient;
import com.gaojy.rice.dispatcher.config.DispatcherConfig;
import com.gaojy.rice.remote.transport.TransfClientConfig;
import com.gaojy.rice.remote.transport.TransportClient;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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

    public RiceDispatchScheduler(TransfClientConfig transfClientConfig, DispatcherConfig dispatcherConfig) {
        this.transfClientConfig = transfClientConfig;
        this.dispatcherConfig = dispatcherConfig;
        electionClient = new ElectionClient(this);
        transportClient = new TransportClient(this.transfClientConfig);
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

        //
    }

    public void shutdown() throws Exception {
        if (jmxConnServer != null) {
            jmxConnServer.stop();
        }
        if (this.transportClient != null) {
            transportClient.shutdown();
        }

    }

    // 发起长轮询获取任务

    // 发送注册

    // 任务调用分发

    // 各种任务类型的初始化  时间轮算法

    // 向所有controller发送心跳

    // 上报processor状态

    // 处理processor 上线

    // 启动banner

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
