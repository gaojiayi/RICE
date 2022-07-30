package com.gaojy.rice.remote.transport;

import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author gaojy
 * @ClassName RiceRequestProcessor.java
 * @Description   业务逻辑处理器接口
 * @createTime 2022/01/01 13:19:00
 */
public interface RiceRequestProcessor {

    RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request)
        throws Exception;

    boolean rejectRequest();
}
