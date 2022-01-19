package com.gaojy.rice.controller.maintain;

import com.gaojy.rice.remote.common.RemoteHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import java.util.Objects;

/**
 * @author gaojy
 * @ClassName ChannelWrapper.java
 * @Description TODO
 * @createTime 2022/01/18 22:57:00
 */
public class ChannelWrapper {
    private final Channel channel;
    private final String remoteAddr;

    public ChannelWrapper(Channel channel) {
        this.channel = channel;
        remoteAddr = RemoteHelper.parseChannelRemoteAddr(channel);
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public boolean isActive() {
        return this.channel != null && this.channel.isActive();
    }

    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ChannelWrapper))
            return false;
        ChannelWrapper wrapper = (ChannelWrapper) o;
        return getRemoteAddr().equals(wrapper.getRemoteAddr());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRemoteAddr());
    }
}
