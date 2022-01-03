package com.gaojy.rice.remote;

import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import java.util.concurrent.ExecutorService;

public interface IBaseRemote extends RemoteService {

    /**
     * @param addr
     * @param request
     * @param timeoutMillis
     * @return com.gaojy.rice.remote.protocol.RiceRemoteContext
     * @throws
     * @description 同步调用
     */
    public RiceRemoteContext invokeSync(final String addr, final RiceRemoteContext request, final long timeoutMillis)
        throws RemotingSendRequestException, RemotingTimeoutException, InterruptedException, RemotingConnectException;

    /**
     * @param addr
     * @param request
     * @param timeoutMillis
     * @param invokeCallback 收到数据后的异步处理，比如信号量处理
     * @throws
     * @description 异步调用
     */
    public void invokeAsync(final String addr, final RiceRemoteContext request, final long timeoutMillis,
        final InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
        RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    /**
     * @param addr
     * @param request
     * @throws
     * @description 单向调用
     */
    public void invokeOneWay(final String addr, final RiceRemoteContext request,final long timeoutMillis)
        throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException,
        RemotingTimeoutException, RemotingSendRequestException;

    /**
     * @param requestCode
     * @param processor
     * @param executor
     * @throws
     * @description 注册业务处理器，即收到响应后调用的业务逻辑
     */
    public void registerProcessor(final int requestCode, final RiceRequestProcessor processor,
        final ExecutorService executor);
}
