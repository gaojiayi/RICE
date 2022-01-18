package com.gaojy.rice.controller.processor;

import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author gaojy
 * @ClassName TaskRegisterAccessProcessor.java
 * @Description 任务处理器注册接入Processor
 * @createTime 2022/01/18 20:52:00
 */
public class TaskRegisterAccessProcessor implements RiceRequestProcessor {

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {
        return null;
    }

    @Override public boolean rejectRequest() {
        return false;
    }
}
