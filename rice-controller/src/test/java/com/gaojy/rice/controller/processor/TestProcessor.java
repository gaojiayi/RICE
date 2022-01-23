package com.gaojy.rice.controller.processor;

import com.gaojy.rice.common.TaskType;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.protocol.body.processor.ExportTaskRequestBody;
import com.gaojy.rice.common.protocol.body.processor.TaskDetailData;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskRequestHeader;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.protocol.SerializeType;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import com.gaojy.rice.remote.transport.TransfClientConfig;
import com.gaojy.rice.remote.transport.TransfServerConfig;
import com.gaojy.rice.remote.transport.TransportClient;
import com.gaojy.rice.remote.transport.TransportServer;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName TestTaskAccessorProcessor.java
 * @Description TODO
 * @createTime 2022/01/23 20:23:00
 */
@RunWith(JUnit4.class)
public class TestProcessor {
    private static TransportClient transportClient;

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

    @Test
    public void taskAccessorProcessor() throws InterruptedException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        TransportServer transportServer = this.buildTransportServer();

        transportServer.registerProcessor(RequestCode.REGISTER_PROCESSOR,
            new TaskAccessProcessor(), Executors.newCachedThreadPool());
        transportServer.start();

        ExportTaskRequestHeader header = new ExportTaskRequestHeader();
        header.setNetAddress("127.0.0.1");
        header.setListenPort(1111);
        header.setAppId("testApp");
        RiceRemoteContext request = RiceRemoteContext.createRequestCommand(RequestCode.REGISTER_PROCESSOR, header);

        ExportTaskRequestBody body = new ExportTaskRequestBody();
        List<TaskDetailData> list = new ArrayList<>();

        TaskDetailData data = new TaskDetailData();
        data.setTaskCode("TestTaskCode");
        data.setTaskName("TestTaskName");
        data.setTaskType(TaskType.RICE_BASE_TASK_TYPE);
        data.setClassName("com.gaojy.rice.common.protocol.body.processor.ExportTaskRequestBody");
        list.add(data);
        body.setTasks(list);

        request.setBody(body.encode());
        request.setSerializeTypeCurrentRPC(SerializeType.RICE);

        RiceRemoteContext response = transportClient.invokeSync("localhost:8888", request, 1000 * 10);
        int code = response.getCode();
        Assert.assertEquals(ResponseCode.SUCCESS, code);

    }

}
