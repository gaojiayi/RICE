package com.gaojy.rice.remote;

import com.gaojy.rice.remote.transport.ResponseFuture;

/**
 * @author gaojy
 * @ClassName InvokeCallback.java
 * @Description 接收到响应数据之后，如果是异步调用，则processResponseCommand中执行InvokeCallback回调处理
 * @createTime 2022/01/01 12:47:00
 */
public interface InvokeCallback {
    void operationComplete(final ResponseFuture responseFuture);
}
