package com.gaojy.rice.controller.maintain;

import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author gaojy
 * @ClassName SchedulerManager.java
 * @Description 调度器状态维护
 * @createTime 2022/01/18 22:48:00
 */
public class SchedulerManager {
    private Set<ChannelWrapper> schedulerNodes = new HashSet<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();    //读锁
    private final Lock w = lock.writeLock();    //写锁

    private static volatile SchedulerManager instance = null;

    private SchedulerManager() {
    }

    public static SchedulerManager getManager() {
        if (instance == null) {
            synchronized (SchedulerManager.class) {
                if (instance == null) {
                    instance = new SchedulerManager();
                }
            }

        }
        return instance;
    }

    public List<String> getActiveSchedulerAddr() {
        r.lock();
        List<String> rets = null;
        try {
            rets = schedulerNodes.stream().filter(c -> c.isActive()).
                    map(ChannelWrapper::getRemoteAddr).collect(Collectors.toList());
        } finally {
            r.unlock();
        }
        return rets;
    }

    public void registerScheduler(Channel channel) {
        w.lock();
        try {
            Set<ChannelWrapper> newSchedulerNodes = new HashSet<>(schedulerNodes);
            newSchedulerNodes.add(new ChannelWrapper(channel));
            schedulerNodes = newSchedulerNodes;
        } finally {
            w.unlock();
        }
    }

    public Boolean updateSchedulerStatus(Channel channel){
        // 比如获取最新的心跳时间
        return Boolean.TRUE;
    }




}
