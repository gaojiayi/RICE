package com.gaojy.rice.controller.replicator;

import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import com.gaojy.rice.common.constants.ElectionConstants;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName TestControllerReplicator.java
 * @Description
 * @createTime 2022/08/08 13:02:00
 */
@RunWith(JUnit4.class)
public class TestControllerReplicator {
    ReplicatorServer server1 = null;
    ReplicatorServer server2 = null;
    ReplicatorServer server3 = null;
    final String initConfStr = "127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083";
    final String groupId = ElectionConstants.ELECTION_GROUP_ID;
    final Configuration initConf = new Configuration();

    @Before
    public void setUp() throws IOException, InterruptedException {
        //  解析多个peerid 和 learner
        if (!initConf.parse(initConfStr)) {
            throw new IllegalArgumentException("Fail to parse initConf:" + initConfStr);
        }

        // start server1
        final String dataPath1 = "/tmp/server1";
        final String serverIdStr1 = "127.0.0.1:8081";
        final PeerId serverId1 = new PeerId();
        if (!serverId1.parse(serverIdStr1)) {
            throw new IllegalArgumentException("Fail to parse serverId1:" + serverIdStr1);
        }
        server1 = new ReplicatorServer(dataPath1, groupId, serverId1, initNodeOptions());
        // start server2
        final String dataPath2 = "/tmp/server2";
        final String serverIdStr2 = "127.0.0.1:8082";
        final PeerId serverId2 = new PeerId();
        if (!serverId2.parse(serverIdStr2)) {
            throw new IllegalArgumentException("Fail to parse serverId2:" + serverIdStr2);
        }
        server2 = new ReplicatorServer(dataPath2, groupId, serverId2, initNodeOptions());
        // start server3
        final String dataPath3 = "/tmp/server3";
        final String serverIdStr3 = "127.0.0.1:8083";
        final PeerId serverId3 = new PeerId();
        if (!serverId3.parse(serverIdStr3)) {
            throw new IllegalArgumentException("Fail to parse serverId3:" + serverIdStr3);
        }
        server3 = new ReplicatorServer(dataPath3, groupId, serverId3, initNodeOptions());
        // 等待选举完成
        Thread.sleep(1000 * 10);
        LockSupport.park();
        System.out.println("========================" + server1.getFsm().isLeader()
            + " " + server2.getFsm().isLeader() + " " + server3.getFsm().isLeader());

    }

    @Test
    public void testElection() throws InterruptedException, TimeoutException {
        PeerId leader = getLeader();

        System.out.println("Leader is " + leader);
    }

    @Test
    public void testData() throws InterruptedException, TimeoutException {
        // 找到主服务器
        PeerId leader = getLeader();
        ReplicatorServer leaderServer = null;
        ReplicatorServer flowerServer1 = null;
        ReplicatorServer flowerServer2 = null;
        String leaderIP = leader.toString();
        System.out.println("========================" + leader);

        switch (leaderIP.substring(leaderIP.length() - 1)) {
            case "1":
                leaderServer = server1;
                flowerServer1 = server2;
                flowerServer2 = server3;
                break;
            case "2":
                leaderServer = server2;
                flowerServer1 = server1;
                flowerServer2 = server3;
                break;
            case "3":
                leaderServer = server3;
                flowerServer1 = server1;
                flowerServer2 = server2;
                break;
        }

//        LockSupport.park();
        // leader写入数据
        ControllerDataService leaderService = new ControllerDataServiceImpl(leaderServer);
        leaderService.updateSchedulerData("1.1.1.1", new SchedulerData("1.1.1.1", 10D, 20D, 100), new ControllerClosure() {
            @Override
            public void run(Status status) {
                // 一般会处理RPC的响应
            }
        });
        // 从服务器读取
        //Thread.sleep(1000 * 10);
        ControllerDataService flowerService1 = new ControllerDataServiceImpl(flowerServer1);
        List<Map<String, String>> value1 = flowerService1.querySchedulersData(true, new ControllerClosure() {
            @Override public void run(Status status) {

            }
        });
        ControllerDataService flowerService2 = new ControllerDataServiceImpl(flowerServer2);

        List<Map<String, String>> value2 = flowerService2.querySchedulersData(true, new ControllerClosure() {
            @Override public void run(Status status) {

            }
        });
        //Thread.sleep(10*1000);
        Assert.assertEquals(value1.get(0).toString(), value2.get(0).toString());
    }

    private PeerId getLeader() throws InterruptedException, TimeoutException {
        final Configuration conf = new Configuration();
        if (!conf.parse("127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083")) {
            throw new IllegalArgumentException("Fail to parse conf");
        }
        RouteTable.getInstance().updateConfiguration(groupId, conf);

        final CliClientServiceImpl cliClientService = new CliClientServiceImpl();
        cliClientService.init(new CliOptions());
        // 这个refreshLeader  不会改变主从关系
        if (!RouteTable.getInstance().refreshLeader(cliClientService, groupId, 1000).isOk()) {
            throw new IllegalStateException("Refresh leader failed");
        }

        final PeerId leader = RouteTable.getInstance().selectLeader(groupId);
        return leader;
    }

    private NodeOptions initNodeOptions() {
        final NodeOptions nodeOptions = new NodeOptions();

        // 为了测试,调整 snapshot 间隔等参数
        // 设置选举超时时间为 1 秒
        nodeOptions.setElectionTimeoutMs(1000);
        // 关闭 CLI 服务。
        nodeOptions.setDisableCli(false);
        // 每隔30秒做一次 snapshot
        nodeOptions.setSnapshotIntervalSecs(30);
        // 设置初始集群配置
        nodeOptions.setInitialConf(initConf);
        return nodeOptions;
    }
}
