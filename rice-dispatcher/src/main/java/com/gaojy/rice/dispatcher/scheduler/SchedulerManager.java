package com.gaojy.rice.dispatcher.scheduler;

import com.gaojy.rice.common.entity.ProcessorServerInfo;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import java.util.List;

/**
 * @author gaojy
 * @ClassName SchedulerManager.java
 * @Description TODO
 * @createTime 2022/02/12 13:24:00
 */
public interface SchedulerManager {

    public void addTask(RiceTaskInfo info, List<ProcessorServerInfo> processorServerInfoList);


    public void taskReBalance(String currentScheduler, List<String> schedulerList);

    /**
     * @description   发生了某一个处理器宕机
     * @param address
     * @throws
     */
    public void isolationProcessorForAllTasks(String address);

//    //  当某一个处理器上已经没有taskCode
//    public void isolationProcessorForTask(String address,String taskCode);
//
//    // 当某个处理器重新上线
//    public void taskOnline(String processorAddress, String taskCode);
//
//    public void deleteTask(String taskCode);
//
//    public void updateTask();

    public void onChange(TaskChangeRecord record);
}
