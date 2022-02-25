package com.gaojy.rice.dispatcher.scheduler.tasktype;

import com.gaojy.rice.common.constants.ExecuteType;
import com.gaojy.rice.common.constants.ExecuterMethodName;
import com.gaojy.rice.common.constants.RequestCode;
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
import org.apache.commons.collections.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * MAP任务执行器
 */
public class RiceMapTaskExecuter implements RiceExecuter {

    private TaskScheduleClient client;

    private final Repository repository = ExtensionLoader.getExtensionLoader(Repository.class)
            .getExtension("mysql");

    public RiceMapTaskExecuter(TaskScheduleClient client) {
        this.client = client;

    }

    @Override
    public void execute(Long taskInstanceId) {
        // 集群模式执行map

        // 将结果写入数据库

        // 并行模式执行 processor
    }

    private Map executeMap(Long taskInstanceId) {
        int retryCount = 0;
        String errorMsg = "";
        TaskInstanceInfo instanceInfo = repository.getTaskInstanceInfoDao().getInstance(taskInstanceId);

        while (retryCount++ <= RiceMapTaskExecuter.this.client.getTaskRetryCount()) {
            List<String> processors = client.selectProcessores(this.client.getExecuteType());
            if (CollectionUtils.isNotEmpty(processors)) {
                RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(RequestCode.INVOKE_PROCESSOR, buildMapRequest(processors.get(0), taskInstanceId));
                // 同步调用
                try {
                    RiceRemoteContext response = RiceMapTaskExecuter.this.client.invokeSync(processors.get(0),requestCommand);
                    TaskInvokerResponseHeader responseHeader = (TaskInvokerResponseHeader)response.readCustomHeader();
                    TaskInvokerResponseBody responseBody = TaskInvokerResponseBody.decode(response.getBody(),TaskInvokerResponseBody.class);
                    // TODO
                    break;
                } catch (RemotingConnectException e) {
                    e.printStackTrace();
                } catch (RemotingSendRequestException e) {
                    e.printStackTrace();
                } catch (RemotingTimeoutException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemotingTooMuchRequestException e) {
                    e.printStackTrace();
                }


            } else {

            }


            if (StringUtil.isNotEmpty(errorMsg)){
                instanceInfo.set
            }
        }


    }

    private TaskInvokeRequestHeader buildMapRequest(String processor, Long taskInstanceId) {
        TaskInvokeRequestHeader requestHeader = new TaskInvokeRequestHeader();
        requestHeader.setAppName(client.getAppName());
        requestHeader.setTaskCode(client.getTaskCode());
        requestHeader.setInstanceParameter(client.getParameters());
        requestHeader.setMethodName(ExecuterMethodName.map.name());
        requestHeader.setSchedulerServer(processor);
        requestHeader.setMaxRetryTimes(client.getInstanceRetryCount());
        requestHeader.setTaskInstanceId(taskInstanceId);
        return requestHeader;
    }
}
