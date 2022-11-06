package com.gaojy.rice.demo.processor;

import com.gaojy.rice.processor.api.RiceProcessorManager;

/**
 * @author gaojy
 * @ClassName ProcessorServiceInstance.java
 * @Description
 * @createTime 2022/11/04 22:58:00
 */
public class ProcessorServiceInstance {
    public static void main(String[] args) {
        // 初始化rice处理器管理
        RiceProcessorManager manager = RiceProcessorManager.getManager();
        // 任务暴露
        manager.export();
    }
}
