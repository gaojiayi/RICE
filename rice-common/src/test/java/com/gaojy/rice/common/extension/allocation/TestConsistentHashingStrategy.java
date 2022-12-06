package com.gaojy.rice.common.extension.allocation;

import com.gaojy.rice.common.allocation.ConsistentHashingStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;

@RunWith(JUnit4.class)
public class TestConsistentHashingStrategy {

    static java.util.List<String> activeSchedulerServers;
    static List<String> taskIds;
    static ConsistentHashingStrategy strategy = new ConsistentHashingStrategy();
    static Map<String, List<String>> initRetMap;

    @Before
    public  void init() {
        activeSchedulerServers = new ArrayList<>();
        taskIds = new LinkedList<>();
        activeSchedulerServers.add("192.169.0.1");
        activeSchedulerServers.add("192.169.0.2");
        activeSchedulerServers.add("192.169.0.3");
        activeSchedulerServers.add("192.169.0.4");
        for (Long i = 0L; i < 50L; i++) {
            taskIds.add(i+"");
        }
        initRetMap = strategy.allocate(activeSchedulerServers, taskIds);
    }

    @Test
    public void registerServerTest() {
        String nextServer = strategy.getNextSchedulerServer(activeSchedulerServers, "192.169.0.5");
        List<String> idsOfnextServer = initRetMap.get(nextServer);
        Collections.sort(idsOfnextServer);

        activeSchedulerServers.add("192.169.0.5");
        Map<String, List<String>> newInitRetMap = strategy.allocate(activeSchedulerServers, idsOfnextServer);
        List<String> combina = newInitRetMap.get("192.169.0.5");
        combina.addAll(newInitRetMap.get(nextServer));
        Collections.sort(combina);
        /**
         * 当新的服务器上线之后，任务将会重新分配，保证新节点上分配的任务和器所在hash环下一个节点上的任务总和为之前下一个节点的任务总和
         */
        Assert.assertEquals(idsOfnextServer, combina);


    }

    @Test
    public void downServerTest() {
        String nextServer = strategy.getNextSchedulerServer(activeSchedulerServers, "192.169.0.3");
        // "192.169.0.3" 下一个节点上的任务
        List<String> oldIdsOfnextServer = initRetMap.get(nextServer);
        Collections.sort(oldIdsOfnextServer);

        List<String> idsOfRemoveServer = initRetMap.get("192.169.0.3");

        activeSchedulerServers.remove("192.169.0.3");

        Map<String, List<String>> newInitRetMap = strategy.allocate(activeSchedulerServers,taskIds);
        List<String> newIds = newInitRetMap.get(nextServer);
        newIds.removeAll(idsOfRemoveServer);
        Collections.sort(newIds);

        /**
         * 当删除一个节点的时候，则该节点上的id，都会落到删除节点的下一个节点上。
         */
        Assert.assertEquals(newIds,oldIdsOfnextServer);

    }

}
