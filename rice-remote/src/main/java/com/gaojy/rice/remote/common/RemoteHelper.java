package com.gaojy.rice.remote.common;

import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author gaojy
 * @ClassName RemoteHelper.java
 * @Description TODO
 * @createTime 2022/01/01 14:15:00
 */
public class RemoteHelper {
    public static final String RICE_REMOTING = "RiceRemoting";

    public static String parseSocketAddressAddr(SocketAddress socketAddress) {
        if (socketAddress != null) {
            final String addr = socketAddress.toString();

            if (addr.length() > 0) {
                return addr.substring(1);
            }
        }
        return "";
    }
    public static String parseChannelRemoteAddr(final Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";

        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }

            return addr;
        }

        return "";
    }

    public static SocketAddress string2SocketAddress(final String addr) {
        String[] s = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(s[0], Integer.parseInt(s[1]));
        return isa;
    }
}
