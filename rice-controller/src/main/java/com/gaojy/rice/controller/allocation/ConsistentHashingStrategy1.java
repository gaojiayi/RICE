package com.gaojy.rice.controller.allocation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author gaojy
 * @ClassName ConsistentHashingAllocationStrategy.java
 * @Description 一致性hash  https://www.cnblogs.com/xrq730/p/5186728.html
 * 调度器的下线，会逐个分配其托管的任务  当调度器上线，则根据其所处的位置 把下一个节点的task分摊到新节点上
 * @createTime 2022/01/18 22:40:00
 */
public class ConsistentHashingStrategy1 implements Strategy {
    /**
     * 待添加入Hash环的服务器列表
     */
    private static String[] servers = {
        "192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111",
        "192.168.0.3:111", "192.168.0.4:111"};

    /**
     * key表示服务器的hash值，value表示服务器的名称
     */
    private static SortedMap<Integer, String> sortedMap =
        new TreeMap<Integer, String>();

    /**
     * 程序初始化，将所有的服务器放入sortedMap中
     */
    static {
        for (int i = 0; i < servers.length; i++) {
            int hash = getHash(servers[i]);
            System.out.println("[" + servers[i] + "]加入集合中, 其Hash值为" + hash);
            sortedMap.put(hash, servers[i]);
        }
        System.out.println();
    }

    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
     */
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

    /**
     * 得到应当路由到的结点
     */
    private static String getServer(String node) {
        // 得到带路由的结点的Hash值
        int hash = getHash(node);
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

    public static void main(String[] args) {
        Map<String, List<String>> ret = new HashMap<>();
        for (int i = 0; i < 50; i++) {
            System.out.println(i);
            String server = getServer("127.0.0.1" + i);
            if (server == null) {
                System.out.println();
            }
            ret.putIfAbsent(server, new LinkedList<String>());
            ret.get(server).add("127.0.0.1" + i);
        }
        ret.forEach((k, v) -> {
            System.out.println(k + "路由到的数据有:" + v);
        });

        System.out.println("===============ADD SERVER========");

        int hash = getHash("192.168.0.5:111");
        Integer tail = sortedMap.tailMap(hash).isEmpty() ? sortedMap.firstKey() : sortedMap.tailMap(hash).firstKey();

        Integer head = sortedMap.headMap(hash).isEmpty() ? sortedMap.lastKey() : sortedMap.headMap(hash).lastKey();
        System.out.println("192.168.0.5:111 between " + sortedMap.get(head) + " and " + sortedMap.get(tail));

        sortedMap.put(hash, "192.168.0.5:111");

        System.out.println("===============再次分配========");
        Map<String, List<String>> ret1 = new HashMap<>();
        for (int i = 0; i < 50; i++) {
            String server = getServer("127.0.0.1" + i);
            ret1.putIfAbsent(server, new LinkedList<String>());
            ret1.get(server).add("127.0.0.1" + i);
        }
        ret1.forEach((k, v) -> {
            System.out.println(k + "路由到的数据有:" + v);
        });

        sortedMap.keySet().stream().filter(c -> !c.equals(tail) && !c.equals(hash)).forEach(h -> {
            String server = sortedMap.get(h);
            System.out.println(server + "是非下一节点，没有发生变化？" + ret.get(server).equals(ret1.get(server)));

        });

        System.out.println("=================删除192.168.0.3:111==========");
        Integer hash1 = getHash("192.168.0.3:111");
        sortedMap.remove(hash1);
        Integer tail1 = sortedMap.tailMap(hash).isEmpty() ? sortedMap.firstKey() : sortedMap.tailMap(hash).firstKey();

        Integer head1 = sortedMap.headMap(hash).isEmpty() ? sortedMap.lastKey() : sortedMap.headMap(hash).lastKey();
        System.out.println("192.168.0.3:111 between " + sortedMap.get(head1) + " and " + sortedMap.get(tail1));

        Map<String, List<String>> ret2 = new HashMap<>();
        for (int i = 0; i < 50; i++) {
            String server = getServer("127.0.0.1" + i);
            ret2.putIfAbsent(server, new LinkedList<String>());
            ret2.get(server).add("127.0.0.1" + i);
        }
        ret2.forEach((k, v) -> {
            System.out.println(k + "路由到的数据有:" + v);
        });

        sortedMap.keySet().stream().filter(c -> !c.equals(head1) && !c.equals(tail1) && !c.equals(hash1)).forEach(h -> {
            String server = sortedMap.get(h);
            System.out.println(server + "是非相邻节点，没有发生变化？" + ret1.get(server).equals(ret2.get(server)));

        });

    }

}
