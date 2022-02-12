package com.gaojy.rice.dispatcher.scheduler;

/**
 * @author gaojy
 * @ClassName SchedulerManager.java
 * @Description TODO
 * @createTime 2022/02/12 13:24:00
 */
public interface SchedulerManager {
    public void addTask();

    public void deleteTask(String taskCode);

    public void updateTask();

    public void isolationProcessorForAllTasks(String address);

    public void taskOnline(String processorAddress, String taskCode);
}
