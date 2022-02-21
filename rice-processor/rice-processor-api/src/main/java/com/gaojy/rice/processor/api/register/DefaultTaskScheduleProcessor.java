package com.gaojy.rice.processor.api.register;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.TaskInstanceStatus;
import com.gaojy.rice.common.protocol.body.scheduler.TaskInvokerResponseBody;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;
import com.gaojy.rice.common.protocol.header.scheduler.TaskInvokeRequestHeader;
import com.gaojy.rice.common.protocol.header.scheduler.TaskInvokerResponseHeader;
import com.gaojy.rice.processor.api.ProcessResult;
import com.gaojy.rice.processor.api.RiceProcessorManager;
import com.gaojy.rice.processor.api.invoker.TaskInvoker;
import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.common.TransfUtil;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName DefaultTaskScheduleProcessor.java
 * @Description 处理调度器的调度请求
 * @createTime 2022/01/07 16:18:00
 */
public class DefaultTaskScheduleProcessor implements RiceRequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.CLIENT_LOGGER_NAME);

    private RiceProcessorManager manager;

    public DefaultTaskScheduleProcessor(RiceProcessorManager manager) {
        this.manager = manager;
    }

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {

        switch (request.getCode()) {
            case RequestCode.INVOKE_PROCESSOR:
                TaskInvokeRequestHeader requestHeader = (TaskInvokeRequestHeader) request.decodeCommandCustomHeader(TaskInvokeRequestHeader.class);
                // 获取来自scheduler的数据

                // 根据taskcode来获取对应的invoker
                TaskInvoker invoker = TaskInvoker.getInvoker(requestHeader.getTaskCode());
                // TODO 重试次数判断
                Long startTime = System.currentTimeMillis();
                ProcessResult result = (ProcessResult)invoker.invokeMethod(invoker
                        .getInvokerInstance(requestHeader.getTaskCode()), requestHeader.getMethodName(),
                        new Class[]{}, new Object[]{});
                Long finishTime = System.currentTimeMillis();
                // 根据任务类型,调用处理器的处理方法
                // 包装响应数据
                RiceRemoteContext response = RiceRemoteContext.createResponseCommand(TaskInvokerResponseHeader.class);
                TaskInvokerResponseHeader responseHeader = (TaskInvokerResponseHeader)response.readCustomHeader();
                // SET header

                responseHeader.setFinishTime(finishTime);
                responseHeader.setTaskCode(requestHeader.getTaskCode());
                responseHeader.setRunningTime(finishTime-startTime);
                responseHeader.setRetryTimes();
                responseHeader.setTaskInstanceStatus(TaskInstanceStatus.FINISHED.name());
                // SET body
                TaskInvokerResponseBody responseBody = new TaskInvokerResponseBody();
                responseBody.setResultMap();

                return response;
            case RequestCode.SCHEDULER_HEART_BEAT:
                String address = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
                log.info("Heartbeat probe received from scheduler:{}", address);
            default:
                return null;
        }

    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
