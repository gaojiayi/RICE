package com.gaojy.rice.dispatcher.scheduler.tasktype;

import com.gaojy.rice.dispatcher.scheduler.TaskScheduleClient;
import java.util.concurrent.ExecutorService;

/**
 * @author gaojy
 * @ClassName RiceBasicTaskExecuter.java
 * @Description 基本任务类型执行器
 * @createTime 2022/02/11 13:35:00
 */
public class RiceBasicTaskExecuter implements RiceExecuter {
    private final TaskScheduleClient client;

    public RiceBasicTaskExecuter(TaskScheduleClient client) {
        this.client = client;
    }


    private void processor() {

    }

    @Override
    public void execute() {
        client.getThreadPool().execute(this);

    }

    @Override
    public void run() {
        processor();
    }
}
