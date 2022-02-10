package com.gaojy.rice.controller.processor;

import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.body.processor.ExportTaskRequestBody;
import com.gaojy.rice.common.protocol.body.processor.ExportTaskResponseBody;
import com.gaojy.rice.common.protocol.body.processor.TaskDetailData;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskRequestHeader;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskResponseHeader;
import com.gaojy.rice.controller.RiceController;
import com.gaojy.rice.controller.maintain.SchedulerManager;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import com.gaojy.rice.repository.api.entity.ProcessorServerInfo;
import com.gaojy.rice.repository.api.entity.RiceTaskInfo;
import io.netty.channel.ChannelHandlerContext;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author gaojy
 * @ClassName TaskRegisterAccessProcessor.java
 * @Description 任务处理器注册接入Processor
 * @createTime 2022/01/18 20:52:00
 */
public class TaskAccessProcessor implements RiceRequestProcessor {

    private final RiceController riceController;

    public TaskAccessProcessor(RiceController riceController) {
        this.riceController = riceController;
    }

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

    private RiceRemoteContext registerProcessor(ChannelHandlerContext ctx,
        RiceRemoteContext request) throws RemotingCommandException {
        final RiceRemoteContext response =
            RiceRemoteContext.createResponseCommand(ExportTaskResponseHeader.class);
        ExportTaskRequestHeader requestHeader = (ExportTaskRequestHeader) request.decodeCommandCustomHeader(ExportTaskRequestHeader.class);
        ExportTaskRequestBody exportTaskRequestBody = ExportTaskRequestBody.decode(request.getBody(), ExportTaskRequestBody.class);
        ExportTaskResponseBody responseBody = new ExportTaskResponseBody();
        final HashMap<String, String> map = new HashMap<>();
        //check


        List<ProcessorServerInfo> serverInfos = riceController.getRepository().getProcessorServerInfoDao()
            .getInfosByServer(requestHeader.getNetAddress(), requestHeader.getListenPort());

        // 原有的处理器注册信息失效
        serverInfos.stream().forEach(info -> info.setStatus(0));

        List<String> taskCodes = exportTaskRequestBody.getTasks().stream().map(TaskDetailData::getTaskCode).collect(Collectors.toList());
        taskCodes.stream().forEach(taskCode ->{
            ProcessorServerInfo info = new ProcessorServerInfo();
            info.setStatus(1);
            info.setTaskCode(taskCode);
            info.setAddress(requestHeader.getNetAddress());
            info.setPort(requestHeader.getListenPort());
            info.setCreateTime(new Date());
            info.setLatestActiveTime(new Date());
            info.setVersion("");
            serverInfos.add(info);
        });

        // 写数据库
        riceController.getRepository().getProcessorServerInfoDao().batchCreateOrUpdateInfo(serverInfos);

        // 请求对应的 scheduler server，处理器上线通知(后续调度器只会更新处理器的心跳时间)
        List<RiceTaskInfo> taskinfos = riceController.getRepository().getRiceTaskInfoDao().getInfoByCodes(taskCodes);

        // 并行通知
        List<CompletableFuture<Boolean>> futures = taskinfos.stream().map(taskInfo -> CompletableFuture.supplyAsync(() -> SchedulerManager.getManager()
            .notifyTaskRegister(taskInfo.getSchedulerServer(), taskInfo.getTaskCode(),
                requestHeader.getNetAddress() + ":" + requestHeader.getListenPort()))).collect(Collectors.toList());
        List<Boolean> collect = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        if(collect.contains(Boolean.FALSE)){
            // 有一个通知失败
            // 抛出异常
            response.setCode(ResponseCode.RESPONSE_ERROR);
        }else {
            taskinfos.stream().forEach(info ->{
                map.put(info.getTaskCode(),info.getSchedulerServer());
            });
            response.setCode(ResponseCode.SUCCESS);
            response.setRemark(null);
        }
        responseBody.setTaskSchedulerInfo(map);
        response.setBody(responseBody.encode());
        return response;
    }
}