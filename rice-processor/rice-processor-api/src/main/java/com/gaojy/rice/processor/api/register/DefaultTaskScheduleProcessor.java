package com.gaojy.rice.processor.api.register;

import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.processor.api.RiceProcessorManager;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author gaojy
 * @ClassName DefaultTaskScheduleProcessor.java
 * @Description 处理调度器的调度请求
 * @createTime 2022/01/07 16:18:00
 */
public class DefaultTaskScheduleProcessor implements RiceRequestProcessor {

    private RiceProcessorManager manager;

    public DefaultTaskScheduleProcessor(RiceProcessorManager manager) {
        this.manager = manager;
    }

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {

        switch (request.getCode()) {
            case RequestCode.SCHEDULER_HEART_BEAT:
                return null;
            case RequestCode.INVOKE_PROCESSOR:
                // 获取来自scheduler的数据

                // 根据taskcode来获取对应的invoker

                // 根据任务类型,调用处理器的处理方法

                // 包装响应数据
                return null;
            default:
                return null;
        }


    }

    @Override public boolean rejectRequest() {
        return false;
    }
}
