package com.gaojy.rice.remote;

import com.gaojy.rice.remote.transport.ResponseFuture;

/**
 * @author gaojy
 * @ClassName InvokeCallback.java
 * @Description TODO   接收数据回调处理
 * @createTime 2022/01/01 12:47:00
 */
public interface InvokeCallback {
    void operationComplete(final ResponseFuture responseFuture);
}
