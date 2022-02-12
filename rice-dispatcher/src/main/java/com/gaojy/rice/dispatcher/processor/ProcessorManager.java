package com.gaojy.rice.dispatcher.processor;

import com.gaojy.rice.dispatcher.scheduler.ProcessorNotifyAble;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaojy
 * @ClassName ProcessorManager.java
 * @Description TODO
 * @createTime 2022/02/11 17:24:00
 */
public class ProcessorManager {
    private final Map<String, Set<ProcessorNotifyAble>> processorNotifyMap = new ConcurrentHashMap<>();
//    private static ProcessorManager manager = new ProcessorManager();
//
//    public static ProcessorManager getManager() {
//        return manager;
//    }

    private Set<ProcessorNotifyAble> getProcessorNotifyList(String address) {
        if (processorNotifyMap.get(address) == null) {
            synchronized (processorNotifyMap) {
                if (processorNotifyMap.get(address) == null) {
                    processorNotifyMap.put(address, Collections.synchronizedSet(new HashSet<ProcessorNotifyAble>()));
                }
            }

        }
        return processorNotifyMap.get(address);
    }

    public void addListener(String address, ProcessorNotifyAble able) {
        getProcessorNotifyList(address).add(able);
    }
    public void removeListener(String address, ProcessorNotifyAble able) {
        getProcessorNotifyList(address).remove(able);
    }

    /**
     * @description
     * @param address
     * @param status
     * @throws
     */
    public void notifyStatus(final String address,final int status){
        getProcessorNotifyList(address).forEach(processorNotify ->{
            processorNotify.notifyProcessor(address,status);
        });
    }
}
