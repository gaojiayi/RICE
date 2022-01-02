package com.gaojy.rice.remote.transport;

import com.gaojy.rice.remote.InvokeCallback;
import com.gaojy.rice.remote.common.SemaphoreReleaseOnlyOnce;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author gaojy
 * @ClassName ResponseFuture.java
 * @Description TODO
 * @createTime 2022/01/01 12:51:00
 */
public class ResponseFuture {
    private final int opaque;
    private final long timeoutMillis;
    private final InvokeCallback invokeCallback;
    private final long beginTimestamp = System.currentTimeMillis();
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final SemaphoreReleaseOnlyOnce once;

    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);
    private volatile RiceRemoteContext riceRemoteContext;
    private volatile boolean sendRequestOK = true;
    private volatile Throwable cause;

    public ResponseFuture(int opaque, long timeoutMillis, InvokeCallback invokeCallback,
        SemaphoreReleaseOnlyOnce once) {
        this.opaque = opaque;
        this.timeoutMillis = timeoutMillis;
        this.invokeCallback = invokeCallback;
        this.once = once;
    }

    /**
     * 包装回调只处理一次 ，可能存在网络回调和定时调度并行处理 使用布尔原子类能保证对个线程只处理一次
     */
    public void executeInvokeCallback() {
        if (invokeCallback != null) {
            if (this.executeCallbackOnlyOnce.compareAndSet(false, true)) {
                invokeCallback.operationComplete(this);
            }
        }
    }

    // 释放信号量
    public void release() {
        if (this.once != null) {
            this.once.release();
        }
    }

    public boolean isTimeout() {
        long diff = System.currentTimeMillis() - this.beginTimestamp;
        return diff > this.timeoutMillis;
    }

    public RiceRemoteContext waitResponse(final long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return this.riceRemoteContext;
    }

    public void putResponse(final RiceRemoteContext riceRemoteContext) {
        this.riceRemoteContext = riceRemoteContext;
        this.countDownLatch.countDown();
    }

    public long getBeginTimestamp() {
        return beginTimestamp;
    }

    public boolean isSendRequestOK() {
        return sendRequestOK;
    }

    public void setSendRequestOK(boolean sendRequestOK) {
        this.sendRequestOK = sendRequestOK;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public InvokeCallback getInvokeCallback() {
        return invokeCallback;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public RiceRemoteContext getRiceRemoteContext() {
        return riceRemoteContext;
    }

    public void setRiceRemoteContext(RiceRemoteContext riceRemoteContext) {
        this.riceRemoteContext = riceRemoteContext;
    }

    public int getOpaque() {
        return opaque;
    }

    @Override
    public String toString() {
        return "ResponseFuture [responseCommand=" + riceRemoteContext + ", sendRequestOK=" + sendRequestOK
            + ", cause=" + cause + ", opaque=" + opaque + ", timeoutMillis=" + timeoutMillis
            + ", invokeCallback=" + invokeCallback + ", beginTimestamp=" + beginTimestamp
            + ", countDownLatch=" + countDownLatch + "]";
    }
}
