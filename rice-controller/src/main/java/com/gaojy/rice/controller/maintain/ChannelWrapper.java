package com.gaojy.rice.controller.maintain;

import com.gaojy.rice.remote.common.RemoteHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * @author gaojy
 * @ClassName ChannelWrapper.java
 * @Description TODO
 * @createTime 2022/01/18 22:57:00
 */
public class ChannelWrapper {
    private final Channel channel;

    public ChannelWrapper(Channel channel) {
        this.channel = channel;
    }

    public String getRemoteAddr(){
        return RemoteHelper.parseChannelRemoteAddr(channel);
    }

    public boolean isActive() {
        return this.channel != null && this.channel.isActive();
    }

    public Channel getChannel() {
        return this.channel;
    }


}
