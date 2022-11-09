package com.gaojy.rice.processor.api.log.appender.log4j;

import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.protocol.body.processor.LogReportRequestBody;
import com.gaojy.rice.processor.api.log.appender.ILogHandler;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author gaojy
 * @ClassName RiceLog4jAppender.java
 * @Description log4j appender to support send log to scheduler
 * @createTime 2022/07/27 23:02:00
 */
public class RiceLog4jAppender extends AppenderSkeleton implements ILogHandler {

    @Override
    protected void append(LoggingEvent event) {
        schedulersOfLog.forEach((taskInstanceId, channel) -> {
            try {
                LogReportRequestBody body = new LogReportRequestBody();
                body.setTaskInstanceId(taskInstanceId);
                body.setLogMessage(this.layout.format(event));
                RiceRemoteContext requestBody = RiceRemoteContext.createRequestCommand(RequestCode.LOG_REPORT,null);
                requestBody.setBody(body.encode());
                channel.writeAndFlush(requestBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}
