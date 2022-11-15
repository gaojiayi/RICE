package com.gaojy.rice.dispatcher.scheduler.tasktype;

import com.gaojy.rice.common.constants.TaskType;
import com.gaojy.rice.dispatcher.scheduler.TaskScheduleClient;
import com.sun.org.apache.regexp.internal.RE;

/**
 * @author gaojy
 * @ClassName TaskExecuterFactory.java
 * @Description
 * @createTime 2022/02/13 22:54:00
 */
public class TaskExecuterFactory {

    public static RiceExecuter getExecuter(TaskType taskType, TaskScheduleClient client) {
        switch (taskType) {
            case BASIC_JAVA_INTERNAL:
                return new RiceBasicTaskExecuter(client);
//            case BROADCAST:
//                return new RiceBroadCastTaskExecuter(client);
            case RICE_MAP:
                return new RiceMapTaskExecuter(client);
            case RICE_MAPREDUCE:
                return new RiceMapReduceTaskExecuter(client);
            case HTTP_GET:
            case HTTP_POST:
                return new HttpTaskExecuter(client);
            case SHELL:
                return null;
            case PYTHON:
                return null;
            case RICE_WORKFLOW:
                return null;
            default:
                return null;
        }
    }
}
