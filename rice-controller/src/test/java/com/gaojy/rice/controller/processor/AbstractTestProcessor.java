package com.gaojy.rice.controller.processor;

import com.gaojy.rice.controller.RiceController;
import com.gaojy.rice.controller.config.ControllerConfig;
import com.gaojy.rice.remote.transport.TransfClientConfig;
import com.gaojy.rice.remote.transport.TransfServerConfig;
import com.gaojy.rice.remote.transport.TransportClient;
import com.gaojy.rice.remote.transport.TransportServer;
import org.junit.BeforeClass;
import org.mockito.Spy;

/**
 * @author gaojy
 * @ClassName abstractTestProcessor.java
 * @Description
 * @createTime 2022/08/13 11:34:00
 */
public abstract  class AbstractTestProcessor {


    public static TransportClient transportClient;

    public TransportServer buildTransportServer() throws InterruptedException {
        TransfServerConfig config = new TransfServerConfig();
        TransportServer remotingServer = new TransportServer(config);

        return remotingServer;
    }

    public static TransportClient buildRemotingClient() {
        TransfClientConfig config = new TransfClientConfig();
        TransportClient client = new TransportClient(config);
        client.start();
        return client;
    }



    @BeforeClass
    public static void setup() throws InterruptedException {
        transportClient = buildRemotingClient();

    }

    @Spy
    RiceController riceController = new RiceController(new ControllerConfig(), new TransfServerConfig());

}
