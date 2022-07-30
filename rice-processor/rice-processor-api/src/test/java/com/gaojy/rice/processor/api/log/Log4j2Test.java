package com.gaojy.rice.processor.api.log;

import com.gaojy.rice.processor.api.log.appender.ILogHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName Log4j2Test.java
 * @Description
 * @createTime 2022/07/30 20:39:00
 */
@RunWith(JUnit4.class)
public class Log4j2Test extends AbstractTestCase{
    @Before
    public void init() {
        Configurator.initialize("log4j2", "src/test/resources/log4j2-example.xml");
    }
    @Test
    public void testLog4j2() {
        ILogHandler.schedulersOfLog.put(100L, channel);
        Logger logger = LogManager.getLogger("test");
        for (int i = 0; i < 50; i++) {
            logger.info("log4j2 log message " + i);
        }

    }
}
