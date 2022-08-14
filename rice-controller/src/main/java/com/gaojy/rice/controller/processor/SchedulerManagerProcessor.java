package com.gaojy.rice.controller.processor;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.constants.SchedulerChangeType;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.body.controller.TaskRebalanceRequestBody;
import com.gaojy.rice.common.protocol.body.scheduler.SchedulerHeartBeatBody;
import com.gaojy.rice.common.protocol.body.scheduler.SchedulerPullTaskChangeResponseBody;
import com.gaojy.rice.common.protocol.header.scheduler.SchedulerPullTaskChangeRequestHeader;
import com.gaojy.rice.common.protocol.header.scheduler.SchedulerRegisterRequestHeader;
import com.gaojy.rice.controller.RiceController;
import com.gaojy.rice.controller.longpolling.PullRequest;
import com.gaojy.rice.controller.longpolling.PullTaskRequestHoldService;
import com.gaojy.rice.controller.maintain.ChannelWrapper;
import com.gaojy.rice.controller.maintain.ProcessorManager;
import com.gaojy.rice.controller.maintain.SchedulerManager;
import com.gaojy.rice.controller.maintain.TaskManager;
import com.gaojy.rice.controller.replicator.SchedulerData;
import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName SchedulerManagerProcessor.java
 * @Description 1 处理心跳(包含处理器上报)  2 调度器长轮询支持  3 调度器向主控制器注册(触发任务再分配)
 * @createTime 2022/01/23 19:27:00
 */
public class SchedulerManagerProcessor implements RiceRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LoggerName.CONTROLLER_LOGGER_NAME);

    // private final ReentrantLock heart_beat_lock = new ReentrantLock();

    private final RiceController riceController;

    private SchedulerManager schedulerManager = SchedulerManager.getManager();

    private ProcessorManager processorManager = ProcessorManager.getManager();

    private TaskManager taskManager = TaskManager.getTaskManager();

    public SchedulerManagerProcessor(RiceController riceController) {
        this.riceController = riceController;
    }

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {
        if (riceController.isMaster()) {
            logger.info("The current controller is the master node and process requests from the scheduler");
            switch (request.getCode()) {
                case RequestCode.SCHEDULER_REGISTER: //  在所有的控制器节点上增加channel，并且主控制器触发任务分配
                    return this.registerScheduler(ctx, request);
                case RequestCode.SCHEDULER_HEART_BEAT: // 维护调度器与所有的控制器的心跳 保存处理器状态
                    this.heartBeatHandler(ctx, request);
                    return null;
                case RequestCode.SCHEDULER_PULL_TASK: // 每一个task  都会带着一个时间搓
                    return this.pullTasks(ctx, request);
                default:
                    return null;
            }
        }
        logger.error("The current controller is not the master node and does not process requests from the scheduler");
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    /**
     * @param ctx
     * @param request
     * @return
     */
    private synchronized RiceRemoteContext registerScheduler(ChannelHandlerContext ctx, RiceRemoteContext request) {
        logger.info("A new scheduler has been added. Address: {}", RemoteHelper.parseChannelRemoteAddr(ctx.channel()));
        final RiceRemoteContext response =
            RiceRemoteContext.createResponseCommand(null);
        String remark = null;
        int responseCode = ResponseCode.SYSTEM_ERROR;
        try {

            SchedulerRegisterRequestHeader requestHeader =
                (SchedulerRegisterRequestHeader) request.decodeCommandCustomHeader(
                    SchedulerRegisterRequestHeader.class);
            schedulerManager.addSchedulerIfAbsent(ctx.channel());
            // 获取字段  是否第一次注册  如果不是第一次注册，
            // 比如因为控制器集群发生了选举而导致的再次注册，但是这个时候是不需要通知的其他的调度器的
            if (requestHeader.getFirstRegister()) {
                // 立即通知所有有效的调度器处理rebalance操作
                RiceRemoteContext rebalceRequest = RiceRemoteContext.createRequestCommand(RequestCode.CONTROLLER_TASK_REBALANCE, null);
                rebalceRequest.markOnewayRPC();

                TaskRebalanceRequestBody body = new TaskRebalanceRequestBody();
                body.setChangeScheduler(RemoteHelper.parseChannelRemoteAddr(ctx.channel()));
                body.setSchedulerOpt(SchedulerChangeType.SCHEDULER_ONLINE);

                List<ChannelWrapper> schedulers = schedulerManager.getActiveScheduler();
                List<String> schedulerAddress = schedulers.stream().map(ChannelWrapper::getRemoteAddr).collect(Collectors.toList());
                body.setCurrentActiveSchedulers(schedulerAddress);

                schedulers.forEach(cw -> {
                    body.setCurrentScheduler(cw.getRemoteAddr());
                    rebalceRequest.setBody(body.encode());
                    // 通知所有的调度器进行任务重分配
                    cw.getChannel().writeAndFlush(rebalceRequest).addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture f) throws Exception {
                            if (f.isSuccess()) {
                                logger.info("Successfully sent task reassignment request to scheduler {}", cw.getRemoteAddr());
                            } else {
                                logger.error("Failed to send task reassignment request to scheduler {}", cw.getRemoteAddr());
                            }

                        }
                    });
                });
                responseCode = ResponseCode.SUCCESS;
            } else {
                logger.info("And the scheduler  is not first register and skip notify other schedulers rebalance");
            }
        } catch (Exception e) {
            logger.error("scheduler register exception", e);
            remark = "scheduler register exception" + e.getMessage();

        }

        response.setRemark(remark);
        response.setCode(responseCode);
        return response;

    }

    public synchronized void crashDownScheduler(String schedulerServer) {
        logger.warn("Exception found in scheduler {}", schedulerServer);
        // 当发送调度器宕机  可再次分配任务
        RiceRemoteContext rebalceRequest = RiceRemoteContext.createRequestCommand(RequestCode.CONTROLLER_TASK_REBALANCE, null);
        rebalceRequest.markOnewayRPC();
        TaskRebalanceRequestBody body = new TaskRebalanceRequestBody();
        List<ChannelWrapper> schedulers = schedulerManager.getActiveScheduler();
        List<String> schedulerAddress = schedulers.stream().map(ChannelWrapper::getRemoteAddr).collect(Collectors.toList());
        body.setCurrentActiveSchedulers(schedulerAddress);
        body.setSchedulerOpt(SchedulerChangeType.SCHEDULER_OFFLINE);
        body.setChangeScheduler(schedulerServer);
        schedulers.forEach(cw -> {
            // 通知所有的调度器进行任务重分配
            body.setCurrentScheduler(cw.getRemoteAddr());
            rebalceRequest.setBody(body.encode());
            cw.getChannel().writeAndFlush(rebalceRequest).addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture f) throws Exception {
                    if (f.isSuccess()) {
                        logger.info("Successfully sent task reassignment request to scheduler {}", cw.getRemoteAddr());
                    } else {
                        logger.error("Failed to send task reassignment request to scheduler {}", cw.getRemoteAddr());
                    }

                }
            });
        });
    }

    private void heartBeatHandler(ChannelHandlerContext ctx, RiceRemoteContext request) {
        String remoteAddr = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("Heartbeat probe received from scheduler {}", remoteAddr);
        SchedulerHeartBeatBody body = SchedulerHeartBeatBody.decode(request.getBody(), SchedulerHeartBeatBody.class);

        try {
            body.getProcessorDetailList().forEach(pd -> {
                // 对于处理器的实时状态 暂时不维护
                // processorManager.putProcessorStatus(pd.getAddress(), pd.getLatestActiveTime());
                logger.info("scheduler:{} report processor:{} status is ok and LatestActiveTime is {}",
                    remoteAddr, pd.getAddress() + ":" + pd.getPort(), new Date(pd.getLatestActiveTime()));
            });
            // 保存任务分配调度结果
            SchedulerData schedulerData = new SchedulerData(remoteAddr, body.getCPURate(), body.getMenRate(),
                body.getTaskCodes().size());
            // 更新到raft状态机中
            riceController.getControllerDataService().updateSchedulerData(remoteAddr, schedulerData, null);
            logger.info("scheduler:{},CPU:{}%,Memory:{}%,taskNum:{}", remoteAddr, body.getCPURate(),
                body.getMenRate(), body.getTaskCodes().size());
        } catch (Exception e) {
            logger.error("heartBeatHandler exception", e);
        }
    }

    private RiceRemoteContext pullTasks(ChannelHandlerContext ctx,
        RiceRemoteContext request) throws RemotingCommandException {
        SchedulerPullTaskChangeRequestHeader header = (SchedulerPullTaskChangeRequestHeader) request
            .decodeCommandCustomHeader(SchedulerPullTaskChangeRequestHeader.class);
        // 看一下有没有相关task的change
        List<TaskChangeRecord> changes = riceController.getRepository().getRiceTaskChangeRecordDao().getChanges(header.getTaskCode(), header.getLastTaskChangeTimestamp());
        if (changes != null && changes.size() > 0) {
            RiceRemoteContext responseCommand = RiceRemoteContext.createResponseCommand(null);
            responseCommand.setCode(ResponseCode.SUCCESS);
            SchedulerPullTaskChangeResponseBody body = new SchedulerPullTaskChangeResponseBody();
            body.setTaskChangeRecordList(changes);
            // 获取最新的修改时间作为下次长轮询请求的起始偏移量
            body.setLatestOffset(changes.get(changes.size() - 1).getCreateTime().getTime());
            responseCommand.setBody(body.encode());
            return responseCommand;
        } else {
            logger.info("taskCode:{} has not found any change after {}", header.getTaskCode(),
                new Date(header.getLastTaskChangeTimestamp()));
            // 放到队列中
            PullRequest pullRequest = new PullRequest(request, ctx.channel(),
                PullTaskRequestHoldService.LONG_POLLING_INTERVAL, System.currentTimeMillis(),
                header.getLastTaskChangeTimestamp());
            riceController.getPullTaskRequestHoldService().suspendPullRequest(header.getTaskCode(), pullRequest);
        }

        return null;
    }

}
