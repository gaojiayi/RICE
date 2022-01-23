package com.gaojy.rice.controller.processor;

import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.body.processor.ExportTaskRequestBody;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskRequestHeader;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskResponseHeader;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author gaojy
 * @ClassName TaskRegisterAccessProcessor.java
 * @Description 任务处理器注册接入Processor
 * @createTime 2022/01/18 20:52:00
 */
public class TaskAccessProcessor implements RiceRequestProcessor {

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {
        switch (request.getCode()) {
            case RequestCode.LOG_REPORT:
                return null;
            case RequestCode.REGISTER_PROCESSOR:
                return this.registerProcessor(ctx, request);
        }

        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    private RiceRemoteContext registerProcessor(ChannelHandlerContext ctx, RiceRemoteContext request) throws RemotingCommandException {
        final RiceRemoteContext response =
                RiceRemoteContext.createResponseCommand(ExportTaskResponseHeader.class);
        ExportTaskRequestHeader requestHeader = (ExportTaskRequestHeader) request.decodeCommandCustomHeader(ExportTaskRequestHeader.class);
        ExportTaskRequestBody exportTaskRequestBody = ExportTaskRequestBody.decode(request.getBody(), ExportTaskRequestBody.class);

        // 写数据库

        // 请求对应的 scheduler server，处理器上线通知

        response.setCode(ResponseCode.SUCCESS);
        response.setRemark(null);
        return response;
    }
}
