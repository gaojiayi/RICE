package com.gaojy.rice.controller.longpolling;

import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import io.netty.channel.Channel;

/**
 * @author gaojy
 * @ClassName PullRequest.java
 * @Description TODO
 * @createTime 2022/01/17 14:54:00
 */
public class PullRequest {
    private final RiceRemoteContext riceRemoteContext;
    private final Channel clientChannel;
    private final long timeoutMillis;
    private final long suspendTimestamp;
    private final long pullFromThisOffset;

    public PullRequest(RiceRemoteContext riceRemoteContext, Channel clientChannel, long timeoutMillis,
        long suspendTimestamp, long pullFromThisOffset) {
        this.riceRemoteContext = riceRemoteContext;
        this.clientChannel = clientChannel;
        this.timeoutMillis = timeoutMillis;
        this.suspendTimestamp = suspendTimestamp;
        this.pullFromThisOffset = pullFromThisOffset;
    }

    public RiceRemoteContext getRiceRemoteContext() {
        return riceRemoteContext;
    }

    public Channel getClientChannel() {
        return clientChannel;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public long getSuspendTimestamp() {
        return suspendTimestamp;
    }

    public long getPullFromThisOffset() {
        return pullFromThisOffset;
    }
}
