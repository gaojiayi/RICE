package com.gaojy.rice.dispatcher.allocation;

import java.util.*;

/**
 * @author gaojy
 * @ClassName ConsistentHashingAllocationStrategy.java
 * @Description 一致性hash
 * 调度器的下线，会逐个分配其托管的任务
 * 当调度器上线，则根据其所处的位置 把下一个节点的task分摊到新节点上
 * @createTime 2022/01/18 22:40:00
 */
public class ConsistentHashingStrategy implements Strategy {
    @Override
    public Map<String, List<String>> allocate(List<String> activeSchedulerServers, List<String> taskCodes) {
        Map<String, List<String>> retMap = new HashMap<>();
        SortedMap<Integer, String> sortedMap = generateSortedMap(activeSchedulerServers);
        taskCodes.forEach(code -> {
            String server = getServer(sortedMap, code);
            List<String> newIds = retMap.computeIfAbsent(server, key -> new LinkedList<>());
            newIds.add(code);
        });

        return retMap;
    }

    private static String getServer(SortedMap<Integer, String> sortedMap, String taskCode) {
        // 得到带路由的结点的Hash值
        int hash = getHash(taskCode);
        // 得到大于该Hash值的所有Map
        SortedMap<Integer, String> subMap =
                sortedMap.tailMap(hash);
        // 第一个Key就是顺时针过去离node最近的那个结点
        // Integer i = subMap.firstKey();
        // 并得到第一个大于此key的项目的key,也就是距离最近的key
        Integer i = subMap.isEmpty() ? sortedMap.firstKey() : subMap.firstKey();
        // 返回对应的服务器名称
        return sortedMap.get(i);
    }

    /**
     * 当有调度器上线时，新的调度器所属hash环的下一个节点上的任务将会重新分配给新的server
     *
     * @param currentSchedulerServers
     * @return
     */
    public String getNextSchedulerServer(List<String> currentSchedulerServers, String newServer) {
        Integer hash = getHash(newServer);
        if(currentSchedulerServers.contains(newServer)){
            hash++;
        }
        SortedMap<Integer, String> sortedMap = generateSortedMap(currentSchedulerServers);
        Integer nextServerHash = sortedMap.tailMap(hash).isEmpty() ? sortedMap.firstKey() : sortedMap.tailMap(hash).firstKey();
        return sortedMap.get(nextServerHash);

    }

    private SortedMap<Integer, String> generateSortedMap(List<String> currentSchedulerServers) {
        SortedMap<Integer, String> sortedMap = new TreeMap<Integer, String>();
        currentSchedulerServers.forEach(server -> {
            sortedMap.put(getHash(server), server);
        });
        return sortedMap;
    }

    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }
}
