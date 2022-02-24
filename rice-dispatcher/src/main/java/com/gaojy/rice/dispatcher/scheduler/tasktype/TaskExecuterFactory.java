package com.gaojy.rice.dispatcher.scheduler.tasktype;

import com.gaojy.rice.common.constants.TaskType;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.dispatcher.scheduler.TaskScheduleClient;

/**
 * @author gaojy
 * @ClassName TaskExecuterFactory.java
 * @Description TODO
 * @createTime 2022/02/13 22:54:00
 */
public class TaskExecuterFactory {

    public  static RiceExecuter getExecuter(TaskType taskType, TaskScheduleClient client) {
        switch (taskType) {
            case RICE_BASIC:
                return new RiceBasicTaskExecuter(client);
            case RICE_MAP:
                return new RiceMapTaskExecuter(client);
            case RICE_MAPREDUCE:
                return new RiceMapReduceTaskExecuter(client);
            default:
                return null;
        }
    }
}
