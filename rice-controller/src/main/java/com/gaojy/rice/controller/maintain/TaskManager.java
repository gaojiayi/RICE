package com.gaojy.rice.controller.maintain;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务管理器 保存着各个任务在哪个调度器上面运行
 */
public class TaskManager {
    private Map<String, String> tasks = new ConcurrentHashMap<>();

    private static volatile TaskManager taskManager = null;

    private TaskManager() {
    }

    public static TaskManager getTaskManager() {
        if (taskManager == null) {
            synchronized (taskManager) {
                if (taskManager == null) {
                    taskManager = new TaskManager();
                }
            }
        }
        return taskManager;
    }

    public void allocateTaskScheduler(String taskCode, String schedulerServer) {
        tasks.put(taskCode, schedulerServer);
    }

}
