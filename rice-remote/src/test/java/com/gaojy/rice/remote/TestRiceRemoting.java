package com.gaojy.rice.remote;

import com.gaojy.rice.common.annotation.CFNullable;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import com.gaojy.rice.remote.transport.TransfServerConfig;
import com.gaojy.rice.remote.transport.TransfClientConfig;
import com.gaojy.rice.remote.transport.TransportClient;
import com.gaojy.rice.remote.transport.TransportServer;
import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.Executors;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.gaojy.rice.remote.protocol.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author gaojy
 * @ClassName TestRiceRemoting.java
 * @Description TODO
 * @createTime 2022/01/04 00:36:00
 */
@RunWith(JUnit4.class)
public class TestRiceRemoting {
    private static IBaseRemote transportClient;
    private static IBaseRemote transportServer;

    public static TransportServer buildTransportServer() throws InterruptedException {
        TransfServerConfig config = new TransfServerConfig();
        TransportServer remotingServer = new TransportServer(config);
        remotingServer.registerProcessor(0,
            new RiceRequestProcessor() {
                @Override
                public RiceRemoteContext processRequest(ChannelHandlerContext ctx,
                    RiceRemoteContext request) throws Exception {
                    request.setRemark("Hi " + ctx.channel().remoteAddress());
                    return request;
                }

                @Override
                public boolean rejectRequest() {
                    return false;
                }
            }, Executors.newCachedThreadPool());
        remotingServer.start();

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
        transportServer = buildTransportServer();
        transportClient = buildRemotingClient();
    }

    @Test
    public void testInvokeSync() throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, InterruptedException {
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setCount(1);
        requestHeader.setMessageTitle("Welcome");
        RiceRemoteContext request = RiceRemoteContext.createRequestCommand(0, requestHeader);
        request.setSerializeTypeCurrentRPC(SerializeType.RICE);
        RiceRemoteContext response = transportClient.invokeSync("localhost:8888", request, 1000 * 30);
        assertTrue(response != null);
        assertThat(response.getLanguage()).isEqualTo(LanguageCode.JAVA);
        assertThat(response.getExtFields()).hasSize(2);

    }

}

class RequestHeader implements CommandCustomHeader {
    @CFNullable
    private Integer count;

    @CFNullable
    private String messageTitle;

    @Override
    public void checkFields() throws RemotingCommandException {
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }
}

