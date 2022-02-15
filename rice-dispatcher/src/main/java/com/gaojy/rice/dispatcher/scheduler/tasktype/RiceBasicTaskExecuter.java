package com.gaojy.rice.dispatcher.scheduler.tasktype;

import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.dispatcher.scheduler.TaskScheduleClient;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import java.util.List;
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

    @Override
    public void execute() {
        List<String> strings = client.selectProcessores(null);
        strings.stream().forEach(address -> {
            client.getThreadPool().execute(new BasicTaskExecuterRunnable(address));
        });

    }

    public class BasicTaskExecuterRunnable implements Runnable {
        private String processor;

        public BasicTaskExecuterRunnable(String processor) {
            this.processor = processor;
        }

        @Override
        public void run() {
            // 拼接请求函数 RiceRemoteContext
            RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(RequestCode.INVOKE_PROCESSOR, null);
            RiceBasicTaskExecuter.this.client.invoke(processor, requestCommand);
            // 写数据库

        }
    }
}
