package com.gaojy.rice.processor.api;

import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

/**
 * @author gaojy
 * @ClassName TestProcessorManager.java
 * @Description mocktio ref:https://blog.csdn.net/qq_38056704/article/details/120208878
 * @createTime 2022/01/26 14:36:00
 */
@RunWith(MockitoJUnitRunner.class)
public class TestProcessorManager {
    @Spy
    RiceProcessorManager manager = RiceProcessorManager.getManager();

    @Test
    public void testRegister() {
        doNothing().when(manager).doRegister(any(RiceRemoteContext.class));
        manager.export();
    }
}
