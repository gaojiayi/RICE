package com.gaojy.rice.processor.api.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.gaojy.rice.processor.api.log.appender.ILogHandler;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName LogBackTest.java
 * @Description
 * @createTime 2022/07/30 20:50:00
 */
@RunWith(JUnit4.class)
public class LogBackTest extends AbstractTestCase {
    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

    @Before
    public void init() throws JoranException {
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        configurator.doConfigure(new File("src/test/resources/logback-example.xml"));
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }
    @Test
    public void testLogback() throws InterruptedException {
        ILogHandler.schedulersOfLog.put(100L, channel);
        Logger logger = lc.getLogger("testLogger");
        for (int i = 0; i < 50; i++) {
            logger.debug("logback test message " + i);
        }
        Thread.sleep(5*1000L);
    }
}
