package com.gaojy.rice.controller.processor;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.constants.TaskOptType;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.body.processor.ExportTaskRequestBody;
import com.gaojy.rice.common.protocol.body.processor.ExportTaskResponseBody;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskRequestHeader;
import com.gaojy.rice.common.protocol.header.processor.ExportTaskResponseHeader;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.controller.RiceController;
import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.common.TransfUtil;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import com.gaojy.rice.common.entity.ProcessorServerInfo;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName TaskRegisterAccessProcessor.java
 * @Description 任务处理器注册接入Processor
 * @createTime 2022/01/18 20:52:00
 */
public class TaskAccessProcessor implements RiceRequestProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(LoggerName.CONTROLLER_LOGGER_NAME);
    private final RiceController riceController;

    public TaskAccessProcessor(RiceController riceController) {
        this.riceController = riceController;
    }

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {
        if (riceController.isMaster()) {
            LOG.info("The current controller is the master node and process requests from the processor");
            switch (request.getCode()) {
                case RequestCode.REGISTER_PROCESSOR:
                    return this.registerProcessor(ctx, request);

            }
        } else {
            LOG.error("The current controller is not the master node and does not process requests from the processor");

        }

        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    // TODO: DB事务控制
    private RiceRemoteContext registerProcessor(ChannelHandlerContext ctx,
        RiceRemoteContext request) throws RemotingCommandException {
        int responseCode = ResponseCode.SYSTEM_ERROR;
        String remark = null;
        final RiceRemoteContext response =
            RiceRemoteContext.createResponseCommand(ExportTaskResponseHeader.class);
        final  ExportTaskResponseBody responseBody = new ExportTaskResponseBody();

        ExportTaskRequestHeader requestHeader = (ExportTaskRequestHeader) request.decodeCommandCustomHeader(ExportTaskRequestHeader.class);
        ExportTaskRequestBody exportTaskRequestBody = ExportTaskRequestBody.decode(request.getBody(), ExportTaskRequestBody.class);
        final String remote = RemoteHelper.parseChannelRemoteAddr(ctx.channel()).split(":")[0];
        if (requestHeader != null && exportTaskRequestBody != null) {
            LOG.info("Processor register to rice controller,appId:{},address:{},tasks:{}",
                requestHeader.getAppId(),
                remote + ":" + requestHeader.getListenPort(),
                exportTaskRequestBody.getTasks());
            //check  根据appid 和 taskCode 查询数据库   检查是否已经配置好了任务
            List<String> taskCodes = exportTaskRequestBody.getTasks().stream().map(item -> item.getTaskCode()).collect(Collectors.toList());
            List<RiceTaskInfo> infoByCodes = riceController.getRepository().getRiceTaskInfoDao().getInfoByCodes(taskCodes);
            if (infoByCodes != null && infoByCodes.size() > 0) {
                boolean allMatch = infoByCodes.stream().allMatch(item -> {
                    responseBody.setTaskSchedulerInfo(item.getTaskCode(),item.getSchedulerServer());
                    return item.getAppId() == Long.parseLong(requestHeader.getAppId());
                });
                if (!allMatch) {
                    remark = "The registered task code does not match the app ID, please check!";
                }
            } else {
                remark = "No task information found on the controller, please create a task from the console";
            }

            if (StringUtil.isEmpty(remark)) {
                try {
                    // 查询之前服务的注册信息  如果二次注册改变了端口 那么属于新注册
                    List<ProcessorServerInfo> serverInfos = riceController.getRepository().getProcessorServerInfoDao()
                        .getInfosByServer(Long.parseLong(requestHeader.getAppId()),
//                            requestHeader.getNetAddress(),
                            remote,
                            requestHeader.getListenPort());
                    Long currentTime = System.currentTimeMillis();

                    List<TaskChangeRecord> records = new ArrayList<>();

                    // 如果存在之前注册的taskcode在这次注册中没有，则需要在注册信息中下线该任务
                    serverInfos.forEach(info -> {
                        info.setStatus(0);
                        if (!taskCodes.contains(info.getTaskCode())) {
                            // 添加涉及到的任务下线记录
                            TaskChangeRecord record = new TaskChangeRecord();
                            record.setCreateTime(new Date(currentTime));
                            record.setTaskCode(info.getTaskCode());
                            record.setOptType(TaskOptType.TASK_PROCESSOR_ISOLATION.getCode());
                            records.add(record);
                        }
                    });

                    taskCodes.stream().forEach(taskCode -> {
                        ProcessorServerInfo info = new ProcessorServerInfo();
                        info.setStatus(1);
                        info.setTaskCode(taskCode);
                        info.setAddress(remote);
                        info.setPort(requestHeader.getListenPort());
                        info.setCreateTime(new Date());
                        info.setLatestActiveTime(new Date());
                        info.setAppId(Long.parseLong(requestHeader.getAppId()));
//
                        serverInfos.add(info);

                        TaskChangeRecord record = new TaskChangeRecord();
                        record.setCreateTime(new Date(currentTime));
                        record.setTaskCode(info.getTaskCode());
                        record.setOptType(TaskOptType.TASK_PROCESSOR_ONLINE.getCode());
                        records.add(record);
                    });
                    // 写数据库
                    riceController.getRepository().getProcessorServerInfoDao().batchCreateOrUpdateInfo(serverInfos);

                    // 写change表
                    riceController.getRepository().getRiceTaskChangeRecordDao().insert(records);

                    // 触发长轮询到达通知
                    records.forEach(record -> {
                        riceController.getPullTaskRequestHoldService().notifyTaskOccurChange(record.getTaskCode(), currentTime);
                    });
                    responseCode = ResponseCode.SUCCESS;
                    LOG.info("Processor register to rice controller Successfully,appId:{},address:{},tasks:{}",
                        requestHeader.getAppId(),
                        remote + ":" + requestHeader.getListenPort(),
                        exportTaskRequestBody.getTasks());
                } catch (Exception e) {
                    LOG.error("register processor occur error", e);
                    remark = "register processor occur error:" + e.getMessage();
                }
            }

        } else {
            remark = "Not Found processor register information";
        }

        if (remark != null) {
            LOG.error(remark);
        }
        response.setBody(responseBody.encode());
        response.setCode(responseCode);
        response.setRemark(remark);
        return response;
    }
}
