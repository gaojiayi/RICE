package com.gaojy.rice.dispatcher.scheduler.tasktype;

import com.gaojy.rice.common.constants.*;
import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.common.protocol.body.scheduler.TaskInvokerResponseBody;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;
import com.gaojy.rice.common.protocol.header.scheduler.TaskInvokeRequestHeader;
import com.gaojy.rice.common.protocol.header.scheduler.TaskInvokerResponseHeader;
import com.gaojy.rice.dispatcher.scheduler.TaskScheduleClient;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.repository.api.Repository;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @author gaojy
 * @ClassName RiceBasicTaskExecuter.java
 * @Description 基本任务类型执行器  TODO 待优化
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

    /**
     * @description 根据调度类型 是固定延迟还是固定评率  还是 cron 来决定是否是同步 是否是异步
     */
    @Override
    public void execute(Long taskInstanceId) {
        final TaskInstanceInfo mainTaskInstance = repository.getTaskInstanceInfoDao().getInstance(taskInstanceId);
        List<String> processes = client.selectProcessores(this.client.getExecuteType());
        if (this.client.getScheduleType().equals(ScheduleType.FIX_DELAY)) {
            Stream<CompletableFuture<Void>> completableFutureStream = processes.stream().map(ps -> {
                return CompletableFuture.runAsync(() -> {
                    TaskInstanceInfo taskInstance = mainTaskInstance;
                    // 如果是广播执行  则创建子任务，更新数据库
                    if (RiceBasicTaskExecuter.this.client.getExecuteType().equals(ExecuteType.BROADCAST)) {
                        taskInstance = RiceBasicTaskExecuter.this.client.buildNewTaskInstance(0L);
                        taskInstance.setStatus(TaskInstanceStatus.RUNNING.getCode());
                        taskInstance.setActualTriggerTime(new Date());
                        taskInstance.setExpectedTriggerTime(mainTaskInstance.getExpectedTriggerTime());
                        taskInstance.setParentInstanceId(mainTaskInstance.getId());
                        Long subTaskInstanceId = repository.getTaskInstanceInfoDao().createTaskInstance(taskInstance);
                        taskInstance.setId(subTaskInstanceId);
                    }
                    // 单机执行  直接更新 mainTaskInstance
                    RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(
                        RequestCode.INVOKE_PROCESSOR, buildRequest(ps, taskInstance.getId()));
                    try {
                        RiceRemoteContext riceRemoteContext = RiceBasicTaskExecuter.this.client.invokeSync(ps, requestCommand);
                        TaskInvokerResponseHeader header = (TaskInvokerResponseHeader) riceRemoteContext.decodeCommandCustomHeader(TaskInvokerResponseHeader.class);
                        TaskInvokerResponseBody resp = TaskInvokerResponseBody.decode(riceRemoteContext.getBody(), TaskInvokerResponseBody.class);
                        taskInstance.setStatus(TaskInstanceStatus.FINISHED.getCode());
                        taskInstance.setFinishedTime(new Date(header.getFinishTime()));
                    } catch (RemotingConnectException |
                        RemotingSendRequestException |
                        InterruptedException |
                        RemotingCommandException |
                        RemotingTooMuchRequestException e) {

                        taskInstance.setStatus(TaskInstanceStatus.EXCEPTION.getCode());
                        taskInstance.setResult(e.getMessage());
                        taskInstance.setFinishedTime(new Date());
                    } catch (RemotingTimeoutException e) {

                        taskInstance.setStatus(TaskInstanceStatus.TIMEOUT.getCode());
                        taskInstance.setResult(e.getMessage());
                        taskInstance.setFinishedTime(new Date());
                    }

                    // 执行完以后再次更新数据库
                    repository.getTaskInstanceInfoDao().updateTaskInstance(taskInstance);
                });
            });
            // 同步等待所有子任务完成
            CompletableFuture.allOf(completableFutureStream.toArray(CompletableFuture[]::new)).whenComplete((v, th) -> {
                //  广播任务 最后更新父任务
                if (this.client.getExecuteType().equals(ExecuteType.BROADCAST)) {
                    mainTaskInstance.setStatus(TaskInstanceStatus.FINISHED.getCode());
                    mainTaskInstance.setFinishedTime(new Date());
                    repository.getTaskInstanceInfoDao().updateTaskInstance(mainTaskInstance);

                }
                log.info("rice basic task execute finally and task_code = {},task_instance_id = {}",
                    mainTaskInstance.getTaskCode(), mainTaskInstance.getId());
            }).join();
        } else {
            //不使用线程池
            processes.stream().forEach(ps -> {
                TaskInstanceInfo taskInstance = mainTaskInstance;
                if (this.client.getExecuteType().equals(ExecuteType.BROADCAST)) {
                    // 如果是广播执行  则创建子任务，更新数据库
                    taskInstance = RiceBasicTaskExecuter.this.client.buildNewTaskInstance(0L);
                    taskInstance.setStatus(TaskInstanceStatus.RUNNING.getCode());
                    taskInstance.setActualTriggerTime(new Date());
                    taskInstance.setExpectedTriggerTime(mainTaskInstance.getExpectedTriggerTime());
                    taskInstance.setParentInstanceId(mainTaskInstance.getId());
                    Long subTaskInstanceId = repository.getTaskInstanceInfoDao().createTaskInstance(taskInstance);
                    taskInstance.setId(subTaskInstanceId);
                }
                RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(
                    RequestCode.INVOKE_PROCESSOR, buildRequest(ps, taskInstance.getId()));
                try {
                    this.client.invokeAsync(ps, requestCommand);
                } catch (RemotingConnectException | RemotingSendRequestException | InterruptedException | RemotingTooMuchRequestException e) {
                    taskInstance.setStatus(TaskInstanceStatus.EXCEPTION.getCode());
                    taskInstance.setResult(e.getMessage());
                    taskInstance.setFinishedTime(new Date());
                } catch (RemotingTimeoutException e) {
                    taskInstance.setStatus(TaskInstanceStatus.TIMEOUT.getCode());
                    taskInstance.setResult(e.getMessage());
                    taskInstance.setFinishedTime(new Date());
                }
                repository.getTaskInstanceInfoDao().updateTaskInstance(taskInstance);
            });

            //  广播任务 最后更新父任务
            if (this.client.getExecuteType().equals(ExecuteType.BROADCAST)) {
                mainTaskInstance.setStatus(TaskInstanceStatus.FINISHED.getCode());
                mainTaskInstance.setFinishedTime(new Date());
                repository.getTaskInstanceInfoDao().updateTaskInstance(mainTaskInstance);

            }

        }

    }

    private TaskInvokeRequestHeader buildRequest(String processor, Long taskInstanceId) {
        TaskInvokeRequestHeader requestHeader = new TaskInvokeRequestHeader();
        requestHeader.setAppId(client.getAppId());
        requestHeader.setTaskCode(client.getTaskCode());
        requestHeader.setInstanceParameter(client.getParameters());
        requestHeader.setMethodName(ExecuterMethodName.process.name());
        requestHeader.setSchedulerServer(processor);
        requestHeader.setMaxRetryTimes(client.getInstanceRetryCount());
        requestHeader.setTaskInstanceId(taskInstanceId);
        return requestHeader;
    }

}
