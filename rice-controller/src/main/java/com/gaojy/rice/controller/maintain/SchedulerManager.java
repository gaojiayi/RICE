package com.gaojy.rice.controller.maintain;

import io.netty.channel.Channel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gaojy
 * @ClassName SchedulerManager.java
 * @Description 调度器状态维护
 * @createTime 2022/01/18 22:48:00
 */
public class SchedulerManager {
    private static volatile Set<ChannelWrapper> schedulerNodes = new HashSet<>();

    public List<String> getActiveSchedulerAddr() {
        return schedulerNodes.stream().filter(c -> c.isActive()).
            map(ChannelWrapper::getRemoteAddr).collect(Collectors.toList());
    }

    public static void registerScheduler(Channel channel){
        if(!schedulerNodes.contains(channel)){
            synchronized (SchedulerManager.class){
                if(!schedulerNodes.contains(channel)){
                    Set<ChannelWrapper> newSchedulerNodes = new HashSet<>(schedulerNodes);
                    newSchedulerNodes.add(new ChannelWrapper(channel));
                    schedulerNodes = newSchedulerNodes;
                }
            }

        }
    }



}
