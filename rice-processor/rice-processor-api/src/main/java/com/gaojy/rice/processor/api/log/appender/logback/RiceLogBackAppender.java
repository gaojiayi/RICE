package com.gaojy.rice.processor.api.log.appender.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.status.ErrorStatus;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.protocol.body.processor.LogReportRequestBody;
import com.gaojy.rice.processor.api.log.appender.ILogHandler;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;

/**
 * @author gaojy
 * @ClassName RiceLogBackAppender.java
 * @Description rice  logback appender
 * @createTime 2022/07/28 22:27:00
 */
public class RiceLogBackAppender extends AppenderBase<ILoggingEvent> implements ILogHandler {

    private Layout layout;

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }
        String logStr = this.layout.doLayout(event);
        schedulersOfLog.forEach((taskInstanceId, channel) -> {
            try {
                LogReportRequestBody body = new LogReportRequestBody();
                body.setTaskInstanceId(taskInstanceId);
                body.setLogMessage(logStr);
                RiceRemoteContext requestBody = RiceRemoteContext.createRequestCommand(RequestCode.LOG_REPORT, null);
                requestBody.setBody(body.encode());
                channel.writeAndFlush(channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Options are activated and become effective only after calling this method.
     */
    public void start() {
        int errors = 0;

        if (this.layout == null) {
            addStatus(new ErrorStatus("No layout set for the RiceLogBackAppender named \"" + name + "\".", this));
            errors++;
        }

        if (errors > 0) {
            return;
        }
        super.start();
    }

    /**
     * When system exit,this method will be called to close resources
     */
    public synchronized void stop() {
        // The synchronized modifier avoids concurrent append and close operations
        if (!this.started) {
            return;
        }

        this.started = false;

    }


    public Layout getLayout() {
        return this.layout;
    }

    /**
     * Set the pattern layout to format the log.
     */
    public void setLayout(Layout layout) {
        this.layout = layout;
    }


}
