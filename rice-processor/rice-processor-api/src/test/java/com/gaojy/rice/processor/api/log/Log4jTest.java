package com.gaojy.rice.processor.api.log;

import com.gaojy.rice.processor.api.log.appender.ILogHandler;
import com.gaojy.rice.remote.transport.TransfClientConfig;
import com.gaojy.rice.remote.transport.TransportClient;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.net.SocketAddress;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName Log4jTest.java
 * @Description
 * @createTime 2022/07/30 19:23:00
 */
@RunWith(JUnit4.class)
public abstract class Log4jTest extends AbstractTestCase{
    @Before
    public abstract void init();

    public abstract String getType();

    @Test
    public void testLog4j()  {
        ILogHandler.schedulersOfLog.put(100L, channel);
        Logger logger = Logger.getLogger("testLogger");
        for (int i = 0; i < 50; i++) {
            logger.info("log4j " + this.getType() + " simple test message " + i);
        }

    }
}
