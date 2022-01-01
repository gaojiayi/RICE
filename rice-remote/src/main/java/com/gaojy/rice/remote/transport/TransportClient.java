package com.gaojy.rice.remote.transport;

import com.gaojy.rice.remote.IBaseRemote;
import com.gaojy.rice.remote.InvokeCallback;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import java.util.concurrent.ExecutorService;

/**
 * @author gaojy
 * @ClassName TransportClient.java
 * @Description TODO
 * @createTime 2022/01/01 14:55:00
 */
public class TransportClient extends AbstractRemoteService implements IBaseRemote {

    @Override
    public RiceRemoteContext invokeSync(String addr, RiceRemoteContext request, long timeoutMillis) {
        return null;
    }

    @Override
    public void invokeAsync(String addr, RiceRemoteContext request, long timeoutMillis,
        InvokeCallback invokeCallback) {

    }

    @Override
    public void invokeOneWay(String addr, RiceRemoteContext request) {

    }

    @Override
    public void registerProcessor(int requestCode, RiceRequestProcessor processor,
        ExecutorService executor) {

    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
