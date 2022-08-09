package com.gaojy.rice.processor.api.log.appender.log4j2;

import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.protocol.body.processor.LogReportRequestBody;
import com.gaojy.rice.processor.api.log.appender.ILogHandler;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.SerializedLayout;

/**
 * @author gaojy
 * @ClassName RIceLog4j2Appender.java
 * @Description 
 * @createTime 2022/07/28 21:17:00
 */
@Plugin(
        name = "Rice",
        category = Node.CATEGORY,
        elementType = Appender.ELEMENT_TYPE,
        printObject = true
)
class RiceLog4j2Appender extends AbstractAppender implements ILogHandler {


    public RiceLog4j2Appender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        final String log = new String(this.getLayout().toByteArray(event), StandardCharsets.UTF_8);
        schedulersOfLog.forEach((taskInstanceId, channel) -> {
            try {
                LogReportRequestBody body = new LogReportRequestBody();
                body.setTaskInstanceId(taskInstanceId);
                body.setLogMessage(log);
                RiceRemoteContext requestBody = RiceRemoteContext.createRequestCommand(RequestCode.LOG_REPORT, null);
                requestBody.setBody(body.encode());
                channel.writeAndFlush(channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * @param timeout
     * @param timeUnit
     * @return boolean
     * @throws
     * @description 当系统退出之后 这个方法被调用去关闭些资源
     */
    public boolean stop(long timeout, TimeUnit timeUnit) {
        this.setStopping();
        // close resources
        boolean stopped = super.stop(timeout, timeUnit, false);
        this.setStopped();
        return stopped;
    }

    /**
     * @description 这个构建器内部的属性  与log xml 配置appender的参数对应
     */
    public static class Builder implements org.apache.logging.log4j.core.util.Builder<RiceLog4j2Appender> {


        @PluginBuilderAttribute
        @Required(message = "A name for the RiceLog4j2Appender must be specified")
        private String name;

        @PluginElement("Layout")
        private Layout<? extends Serializable> layout;

        @PluginElement("Filter")
        private Filter filter;

        @PluginBuilderAttribute
        private boolean ignoreExceptions;

        private Builder() {
            this.layout = SerializedLayout.createLayout();
            this.ignoreExceptions = true;
        }

        public RiceLog4j2Appender.Builder setName(String name) {
            this.name = name;
            return this;
        }

        public RiceLog4j2Appender.Builder setLayout(Layout<? extends Serializable> layout) {
            this.layout = layout;
            return this;
        }

        public RiceLog4j2Appender.Builder setFilter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public RiceLog4j2Appender.Builder setIgnoreExceptions(boolean ignoreExceptions) {
            this.ignoreExceptions = ignoreExceptions;
            return this;
        }

        @Override
        public RiceLog4j2Appender build() {
            return new RiceLog4j2Appender(name, filter, layout, ignoreExceptions);

        }
    }

}
