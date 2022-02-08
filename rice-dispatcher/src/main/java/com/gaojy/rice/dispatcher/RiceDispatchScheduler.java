package com.gaojy.rice.dispatcher;

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

    public RiceDispatchScheduler() {

   }

    // JMX 管理
    public void startJMXManagement() throws Exception {
        int rmiPort = 1099;

        Registry registry = LocateRegistry.createRegistry(rmiPort);
        // Get the Platform MBean Server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // Construct the ObjectName for the Hello MBean we will register
        ObjectName mbeanName = new ObjectName("com.gaojy.rice.dispatcher:type=scheduler");

        // Create the Hello World MBean
        //RiceScheduleMonitor mbean = new RiceScheduleMonitor();

        // Register the Hello World MBean
        mbs.registerMBean(this, mbeanName);

        String url = "service:jmx:rmi://localhost:1010/jndi/rmi://localhost:" + rmiPort + "/jmxrmi";

        JMXServiceURL jmxUrl = new JMXServiceURL(url);
        JMXConnectorServer jmxConnServer = JMXConnectorServerFactory.newJMXConnectorServer(jmxUrl, null, mbs);

        jmxConnServer.start();
    }

    public void start() throws Exception {
        startJMXManagement();

        // 长轮询

        //
    }

    // 发起长轮询获取任务

    // 发送注册

    // 任务调用分发

    // 各种任务类型的初始化  时间轮算法

    // 向所有controller发送心跳

    // 上报processor状态

    // 处理processor 上线

    // 启动banner

}
