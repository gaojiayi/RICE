package com.gaojy.rice.remote;

import io.netty.channel.Channel;

/**
 * @author gaojy
 * @ClassName ChannelEventListener.java
 * @Description TODO
 * @createTime 2022/01/02 10:31:00
 */
public interface ChannelEventListener {
    void onChannelConnect(final String remoteAddr, final Channel channel);

    void onChannelClose(final String remoteAddr, final Channel channel);

    void onChannelException(final String remoteAddr, final Channel channel);

    void onChannelIdle(final String remoteAddr, final Channel channel);
}
