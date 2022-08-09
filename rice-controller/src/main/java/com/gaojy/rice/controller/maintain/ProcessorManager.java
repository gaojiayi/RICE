package com.gaojy.rice.controller.maintain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理器状态维护
 */
public class ProcessorManager {
    private Map<String /* ip:port */, Long/* 最近一次心跳的时间搓*/> processors = new ConcurrentHashMap<>();

    private static volatile ProcessorManager processorManager = null;

    private ProcessorManager() {
    }

    public static ProcessorManager getManager() {
        if (processorManager == null) {
            synchronized (ProcessorManager.class) {
                if (processorManager == null) {
                    processorManager = new ProcessorManager();
                }
            }
        }

        return processorManager;
    }

    public void  putProcessorStatus(String address,Long latestActiveTime){
        processors.put(address,latestActiveTime);
    }
}
