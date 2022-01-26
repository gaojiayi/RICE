package com.gaojy.rice.processor.api;

import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskResponseHeader;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.TransportClient;
import java.util.HashMap;
import javax.print.attribute.standard.JobOriginatingUserName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * @author gaojy
 * @ClassName TestProcessorManager.java
 * @Description TODO
 * @createTime 2022/01/26 14:36:00
 */
@RunWith(MockitoJUnitRunner.class)
public class TestProcessorManager {

    @Test
    public void testRegister() throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, InterruptedException {
        RiceProcessorManager.getManager().export();
    }
}
