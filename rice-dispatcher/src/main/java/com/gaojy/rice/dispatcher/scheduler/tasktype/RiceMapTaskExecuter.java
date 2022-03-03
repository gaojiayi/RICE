package com.gaojy.rice.dispatcher.scheduler.tasktype;

import com.alibaba.fastjson.JSON;
import com.gaojy.rice.common.constants.ExecuteType;
import com.gaojy.rice.common.constants.ExecuterMethodName;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.TaskInstanceStatus;
import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.common.extension.ExtensionLoader;
import com.gaojy.rice.common.protocol.body.scheduler.TaskInvokerResponseBody;
import com.gaojy.rice.common.protocol.header.scheduler.TaskInvokeRequestHeader;
import com.gaojy.rice.common.protocol.header.scheduler.TaskInvokerResponseHeader;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.dispatcher.scheduler.TaskScheduleClient;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.repository.api.Repository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.collections.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * MAP任务执行器
 */
public class RiceMapTaskExecuter extends AbstractTaskExecuter implements RiceExecuter {

    public RiceMapTaskExecuter(TaskScheduleClient client) {
        super(client);
    }

    @Override
    public void execute(Long taskInstanceId) {

        TaskInstanceInfo mapTaskInstance = buildMapTaskInstance(taskInstanceId);
        repository.getTaskInstanceInfoDao().createTaskInstance(mapTaskInstance);
        Future<Map> future = client.getThreadPool().submit(new StandaloneTaskRunnable(mapTaskInstance));
        Map retMap = null;
        try {
            retMap = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (retMap != null && !retMap.isEmpty()) {
            final CountDownLatch latch = new CountDownLatch(retMap.size());
            // 并行模式执行 processor

            retMap.forEach((k, v) -> {
                TaskInstanceInfo info = buildProcessTaskInstance(taskInstanceId, String.valueOf(v));
                client.getThreadPool().submit(new StandaloneTaskRunnable(info, latch));
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        // 更新数据库
        TaskInstanceInfo rootInstance = repository.getTaskInstanceInfoDao().getInstance(taskInstanceId);
        rootInstance.setFinishedTime(new Date());
        rootInstance.setStatus(TaskInstanceStatus.FINISHED.getCode());
        repository.getTaskInstanceInfoDao().updateTaskInstance(rootInstance);

    }

    private TaskInstanceInfo buildProcessTaskInstance(Long parentInstanceId, String value) {
        TaskInstanceInfo info = new TaskInstanceInfo();
        info.setType(ExecuterMethodName.process.name());
        info.setParentInstanceId(parentInstanceId);
        info.setInstanceParams(value);
        info.setTaskCode(client.getTaskCode());
        info.setStatus(TaskInstanceStatus.WAIT.getCode());
        info.setCreateTime(new Date());
        info.setExpectedTriggerTime(new Date());
        info.setActualTriggerTime(new Date());
        return info;
    }

    private TaskInstanceInfo buildMapTaskInstance(Long parentInstanceId) {
        TaskInstanceInfo info = new TaskInstanceInfo();
        info.setType(ExecuterMethodName.map.name());
        info.setParentInstanceId(parentInstanceId);
        info.setInstanceParams(client.getParameters());
        info.setTaskCode(client.getTaskCode());
        info.setStatus(TaskInstanceStatus.WAIT.getCode());
        info.setCreateTime(new Date());
        info.setExpectedTriggerTime(new Date());
        info.setActualTriggerTime(new Date());
        return info;
    }

}
