package com.gaojy.rice.processor.api;

import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

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
