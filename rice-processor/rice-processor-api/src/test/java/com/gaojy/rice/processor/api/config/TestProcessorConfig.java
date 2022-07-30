package com.gaojy.rice.processor.api.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
/**
 * @author gaojy
 * @ClassName TestProcessorConfig.java
 * @Description
 * @createTime 2022/07/30 16:11:00
 */
@RunWith(JUnit4.class)
public class TestProcessorConfig {

    @Test
    public void testConfig(){
        ProcessorConfig config = new ProcessorConfig();
        assertTrue(config.getAppId().equals("101"));
        assertTrue(config.getTransfServerConfig().getServerWorkerThreads() == 16);
    }

}
