package com.gaojy.rice.dispatcher.processor;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.entity.RiceLog;
import com.gaojy.rice.common.protocol.body.processor.LogReportRequestBody;
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
 * @ClassName TaskLogProcessor.java
 * @Description
 * @createTime 2022/11/07 20:52:00
 */
public class TaskLogProcessor implements RiceRequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.DISPATCHER_LOGGER_NAME);
    private final SchedulerManager schedulerManager;

    public TaskLogProcessor(SchedulerManager schedulerManager) {
        this.schedulerManager = schedulerManager;
    }

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {
        LogReportRequestBody body = LogReportRequestBody.decode(request.getBody(), LogReportRequestBody.class);
        RiceLog riceLog = new RiceLog();
        riceLog.setTaskInstanceId(body.getTaskInstanceId());
        riceLog.setProcessorAddr(RemoteHelper.parseChannelRemoteAddr(ctx.channel()));
        riceLog.setSchedulerAddr(ctx.channel().localAddress().toString().replace("/",""));
        riceLog.setMessage(body.getLogMessage());
        this.schedulerManager.appendLog(riceLog);
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
