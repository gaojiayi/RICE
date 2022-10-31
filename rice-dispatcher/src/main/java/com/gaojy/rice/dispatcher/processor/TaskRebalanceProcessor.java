package com.gaojy.rice.dispatcher.processor;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.protocol.body.controller.TaskRebalanceRequestBody;
import com.gaojy.rice.dispatcher.scheduler.SchedulerManager;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName TaskRebalanceProcessor.java
 * @Description 当任何一个调度器上线或者下线，主控制器都会下发rebalance通知
 * @createTime 2022/02/13 12:46:00
 */
public class TaskRebalanceProcessor implements RiceRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerName.DISPATCHER_LOGGER_NAME);
    private final SchedulerManager schedulerManager;

    public TaskRebalanceProcessor(SchedulerManager schedulerManager) {
        this.schedulerManager = schedulerManager;
    }

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {
        // 获取所有正常的调度器
        TaskRebalanceRequestBody requestBody = TaskRebalanceRequestBody
            .decode(request.getBody(), TaskRebalanceRequestBody.class);
        RiceRemoteContext response = RiceRemoteContext.createResponseCommand(null);
        try {
            schedulerManager.taskReBalance(requestBody.getCurrentScheduler()
                , requestBody.getCurrentActiveSchedulers());
            response.setCode(ResponseCode.SUCCESS);
            response.setRemark(null);
        } catch (Exception e) {
            LOGGER.error("handle task re-balance exception,{}", e);
            response.setCode(ResponseCode.RESPONSE_ERROR);
            response.setRemark(e.getMessage());
        }
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
