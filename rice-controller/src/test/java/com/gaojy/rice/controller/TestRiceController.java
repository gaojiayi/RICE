package com.gaojy.rice.controller;

import com.gaojy.rice.controller.config.ControllerConfig;
import com.gaojy.rice.remote.transport.TransfServerConfig;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName TestRiceController.java
 * @Description 
 * @createTime 2022/01/26 20:37:00
 */
@RunWith(JUnit4.class)
public class TestRiceController {

    @Test
    public void testStart() throws IOException {
        ControllerConfig config = new ControllerConfig();
        TransfServerConfig serverConfig = new TransfServerConfig();
        serverConfig.setListenPort(8881);
        RiceController controller = new RiceController(config, serverConfig);
        controller.start();
        controller.shutdown();
    }
}
