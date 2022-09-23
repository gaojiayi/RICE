package com.gaojy.rice.dispatcher.processor;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.protocol.body.controller.TaskCreateRequestBody;
import com.gaojy.rice.dispatcher.scheduler.SchedulerManager;
import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.common.TransfUtil;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName TaskCreateProcessor.java
 * @Description 新任务的增加  一般是由前端触发  通过控制器通知到指定的调度器中
 * @createTime 2022/02/13 13:22:00
 */
public class TaskCreateProcessor implements RiceRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.DISPATCHER_LOGGER_NAME);
    private final SchedulerManager schedulerManager;

    public TaskCreateProcessor(SchedulerManager schedulerManager) {
        this.schedulerManager = schedulerManager;
    }

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {
        TaskCreateRequestBody requestBody = TaskCreateRequestBody.decode(request.getBody(), TaskCreateRequestBody.class);
        RiceRemoteContext response = RiceRemoteContext.createResponseCommand(null);
        try {
            schedulerManager.addTask(RemoteHelper.parseChannelRemoteAddr(ctx.channel()),
                requestBody.getTaskInfo(), requestBody.getProcessores());
            response.setCode(ResponseCode.SUCCESS);
            response.setRemark(null);
        } catch (Exception e) {
            log.error("Failed to add task，request:{}", request);
            response.setCode(ResponseCode.RESPONSE_ERROR);
            response.setRemark(e.getMessage());
        }
        return response;
    }

    @Override public boolean rejectRequest() {
        return false;
    }
}
