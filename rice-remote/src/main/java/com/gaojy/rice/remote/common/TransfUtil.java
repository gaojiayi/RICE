package com.gaojy.rice.remote.common;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName TransfUtil.java
 * @Description TODO
 * @createTime 2022/01/02 11:00:00
 */
public class TransfUtil {
    public static final String OS_NAME = System.getProperty("os.name");

    private static final Logger log = LoggerFactory.getLogger(RemoteHelper.RICE_REMOTING);
    private static boolean isLinuxPlatform = false;
    private static boolean isWindowsPlatform = false;

    static {
        if (OS_NAME != null && OS_NAME.toLowerCase().contains("linux")) {
            isLinuxPlatform = true;
        }

        if (OS_NAME != null && OS_NAME.toLowerCase().contains("windows")) {
            isWindowsPlatform = true;
        }
    }

    public static boolean isLinuxPlatform() {
        return isLinuxPlatform;
    }

    public static boolean isWindowsPlatform() {
        return isWindowsPlatform;
    }

    public static void closeChannel(Channel channel) {
        final String addrRemote = RemoteHelper.parseChannelRemoteAddr(channel);
        channel.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("closeChannel: close the connection to remote address[{}] result: {}", addrRemote,
                    future.isSuccess());
            }
        });
    }
}
