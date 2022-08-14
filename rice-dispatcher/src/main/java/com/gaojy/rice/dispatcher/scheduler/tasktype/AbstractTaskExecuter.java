package com.gaojy.rice.dispatcher.scheduler.tasktype;

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
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author gaojy
 * @ClassName TaskExecuter.java
 * @Description 
 * @createTime 2022/02/11 13:36:00
 */
public abstract class AbstractTaskExecuter {

    TaskScheduleClient client;

    final Repository repository = ExtensionLoader.getExtensionLoader(Repository.class)
        .getExtension("mysql");

    public AbstractTaskExecuter(TaskScheduleClient client) {
        this.client = client;
    }

    //单机任务runnable
    public class StandaloneTaskRunnable implements Callable<Map> {
        CountDownLatch cdl;
        TaskInstanceInfo instanceInfo;
        public StandaloneTaskRunnable(TaskInstanceInfo instanceInfo) {
            this(instanceInfo, null);
        }

        public StandaloneTaskRunnable(TaskInstanceInfo instanceInfo, final CountDownLatch cdl) {
            this.cdl = cdl;
            this.instanceInfo = instanceInfo;
        }

        @Override
        public Map call() throws Exception{
            Map retMap = null;
            int retryCount = 0;
            String errorMsg = "";
            while (retryCount++ <= AbstractTaskExecuter.this.client.getTaskRetryCount()) {
                String processor = client.selectOneProcessor();
                RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(RequestCode.INVOKE_PROCESSOR, buildRequest(processor));
                // 同步调用
                try {
                    RiceRemoteContext response = AbstractTaskExecuter.this.client.invokeSync(processor, requestCommand);
                    TaskInvokerResponseHeader responseHeader = (TaskInvokerResponseHeader) response.readCustomHeader();
                    TaskInvokerResponseBody responseBody = TaskInvokerResponseBody.decode(response.getBody(), TaskInvokerResponseBody.class);
                    retMap = responseBody.getResultMap();
                    instanceInfo.setStatus(TaskInstanceStatus.FINISHED.getCode());
                    instanceInfo.setRunningTimes(responseHeader.getRetryTimes());
                    instanceInfo.setResult(responseBody.toJson());
                    instanceInfo.setFinishedTime(new Date(responseHeader.getFinishTime()));
                    break;
                } catch (RemotingConnectException | RemotingSendRequestException | RemotingTimeoutException
                    | InterruptedException | RemotingTooMuchRequestException e) {
                    errorMsg = e.getMessage();
                }
            }
            instanceInfo.setRunningTimes(retryCount - 1);
            if (StringUtil.isNotEmpty(errorMsg)) {
                instanceInfo.setResult(errorMsg);
                instanceInfo.setStatus(TaskInstanceStatus.EXCEPTION.getCode());
            }
            AbstractTaskExecuter.this.repository.getTaskInstanceInfoDao().updateTaskInstance(instanceInfo);

            if(cdl != null){
                cdl.countDown();
            }
            return retMap;

        }

        private TaskInvokeRequestHeader buildRequest(String processor) {
            TaskInvokeRequestHeader requestHeader = new TaskInvokeRequestHeader();
            requestHeader.setAppName(AbstractTaskExecuter.this.client.getAppName());
            requestHeader.setTaskCode(instanceInfo.getTaskCode());
            requestHeader.setInstanceParameter(instanceInfo.getInstanceParams());
            requestHeader.setMethodName(instanceInfo.getType());
            requestHeader.setSchedulerServer(processor);
            requestHeader.setMaxRetryTimes(client.getInstanceRetryCount());
            requestHeader.setTaskInstanceId(instanceInfo.getId());
            return requestHeader;
        }


    }

}
