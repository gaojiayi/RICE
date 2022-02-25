package com.gaojy.rice.dispatcher.scheduler.tasktype;

import com.gaojy.rice.common.constants.*;
import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.common.protocol.header.scheduler.TaskInvokeRequestHeader;
import com.gaojy.rice.dispatcher.scheduler.TaskScheduleClient;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.repository.api.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author gaojy
 * @ClassName RiceBasicTaskExecuter.java
 * @Description 基本任务类型执行器
 * @createTime 2022/02/11 13:35:00
 */
public class RiceBasicTaskExecuter implements RiceExecuter {

    private final static Logger log = LoggerFactory.getLogger(LoggerName.DISPATCHER_LOGGER_NAME);

    private final TaskScheduleClient client;

    private final Repository repository = ExtensionLoader.getExtensionLoader(Repository.class)
            .getExtension("mysql");

    public RiceBasicTaskExecuter(TaskScheduleClient client) {
        this.client = client;
    }

    @Override
    public void execute(Long taskInstanceId) {
        List<String> strings = client.selectProcessores(ExecuteType.STANDALONE);
        strings.stream().forEach(address -> {
            client.getThreadPool().execute(new BasicTaskExecuterRunnable(address, taskInstanceId));
        });
    }

    public class BasicTaskExecuterRunnable implements Runnable {
        private String processor;
        private Long taskInstanceId;
        private int retryCount = 0;

        public BasicTaskExecuterRunnable(String processor, Long taskInstanceId) {
            this.processor = processor;
            this.taskInstanceId = taskInstanceId;
        }

        @Override
        public void run() {
            RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(RequestCode.INVOKE_PROCESSOR, buildRequest());
            while (retryCount++ <= RiceBasicTaskExecuter.this.client.getTaskRetryCount()) {
                try {
                    RiceBasicTaskExecuter.this.client.invokeAsync(processor, requestCommand);
                    return;
                } catch (RemotingConnectException | RemotingSendRequestException
                    | RemotingTimeoutException | InterruptedException
                    | RemotingTooMuchRequestException e) {
                    // 单机模式  每次重试都要重新选择处理器
                    if (RiceBasicTaskExecuter.this.client.getExecuteType().equals(ExecuteType.STANDALONE)) {
                        List<String> strings = client.selectProcessores(ExecuteType.STANDALONE);
                        strings.stream().forEach(address -> {
                            BasicTaskExecuterRunnable.this.setProcessor(address);
                            RiceBasicTaskExecuter.this.client.getThreadPool().execute(BasicTaskExecuterRunnable.this);
                        });
                    }
                    // 广播模式  对同一个处理器执行重试
                    if(RiceBasicTaskExecuter.this.client.getExecuteType().equals(ExecuteType.BROADCAST)){
                        RiceBasicTaskExecuter.this.client.getThreadPool().execute(BasicTaskExecuterRunnable.this);
                    }
                }
            }

            log.error(",taskCode:{},taskInstanceId:{},the maximum number of retries has been exceeded.", RiceBasicTaskExecuter.this.client.getTaskCode(), taskInstanceId);
            // 更新数据库
            TaskInstanceInfo taskInstanceInfo = repository.getTaskInstanceInfoDao().getInstance(taskInstanceId);
            taskInstanceInfo.setStatus(TaskInstanceStatus.EXCEPTION.getCode());
            taskInstanceInfo.setFinishedTime(new Date());
            taskInstanceInfo.setRunningTimes(BasicTaskExecuterRunnable.this.getRetryCount());
            taskInstanceInfo.setTaskTrackerAddress(BasicTaskExecuterRunnable.this.getProcessor());
            repository.getTaskInstanceInfoDao().updateTaskInstance(taskInstanceInfo);

        }

        private TaskInvokeRequestHeader buildRequest() {
            TaskInvokeRequestHeader requestHeader = new TaskInvokeRequestHeader();
            requestHeader.setAppName(client.getAppName());
            requestHeader.setTaskCode(client.getTaskCode());
            requestHeader.setInstanceParameter(client.getParameters());
            requestHeader.setMethodName(ExecuterMethodName.process.name());
            requestHeader.setSchedulerServer(this.processor);
            requestHeader.setMaxRetryTimes(client.getInstanceRetryCount());
            requestHeader.setTaskInstanceId(this.taskInstanceId);
            return requestHeader;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public String getProcessor() {
            return processor;
        }

        public void setProcessor(String processor) {
            this.processor = processor;
        }
    }
}
