package com.gaojy.rice.controller.processor;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.body.controller.TaskRebalanceRequestBody;
import com.gaojy.rice.common.protocol.body.scheduler.SchedulerHeartBeatBody;
import com.gaojy.rice.common.protocol.body.scheduler.SchedulerPullTaskChangeResponseBody;
import com.gaojy.rice.common.protocol.header.scheduler.SchedulerPullTaskChangeRequestHeader;
import com.gaojy.rice.common.protocol.header.scheduler.SchedulerRegisterRequestHeader;
import com.gaojy.rice.controller.RiceController;
import com.gaojy.rice.controller.longpolling.PullRequest;
import com.gaojy.rice.controller.maintain.ChannelWrapper;
import com.gaojy.rice.controller.maintain.ProcessorManager;
import com.gaojy.rice.controller.maintain.SchedulerManager;
import com.gaojy.rice.remote.common.RemoteHelper;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName SchedulerManagerProcessor.java
 * @Description 1 处理心跳(包含处理器上报)  2 调度器拉拉取任务  3 调度器向主控制器注册(触发任务再分配)
 * @createTime 2022/01/23 19:27:00
 */
public class SchedulerManagerProcessor implements RiceRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LoggerName.CONTROLLER_LOGGER_NAME);

    // private final ReentrantLock heart_beat_lock = new ReentrantLock();

    private final RiceController riceController;

    private SchedulerManager schedulerManager = SchedulerManager.getManager();

    private ProcessorManager processorManager = ProcessorManager.getManager();

    public SchedulerManagerProcessor(RiceController riceController) {
        this.riceController = riceController;
    }

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {

        switch (request.getCode()) {
            case RequestCode.SCHEDULER_REGISTER: //  在所有的控制器节点上增加channel，并且主控制器触发任务分配
                this.registerScheduler(ctx, request);
                return null;
            case RequestCode.SCHEDULER_HEART_BEAT: // 维护调度器与所有的控制器的心跳 保存处理器状态
                this.heartBeatHandler(ctx, request);
                return null;
            case RequestCode.SCHEDULER_PULL_TASK: // 每一个task  都会带着一个时间搓
                return this.pullTasks(ctx, request);
            default:
                return null;
        }
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    /**
     * 1 保存调度器管理器内
     * 2 任务的重新分配
     * 3 任务分配结果记录在数据库
     * 4 响应对应长连接的请求
     *
     * @param ctx
     * @param request
     * @return
     */
    public synchronized void registerScheduler(ChannelHandlerContext ctx, RiceRemoteContext request) {
        logger.info("A new scheduler has been added. Address: {}", RemoteHelper.parseChannelRemoteAddr(ctx.channel()));
        schedulerManager.addSchedulerIfAbsent(ctx.channel());
        if (this.riceController.isMaster()) { // 立即通知所有有效的调度器
            RiceRemoteContext rebalceRequest = RiceRemoteContext.createRequestCommand(RequestCode.CONTROLLER_TASK_REBALANCE, null);
            rebalceRequest.markOnewayRPC();
            TaskRebalanceRequestBody body = new TaskRebalanceRequestBody();
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
        }

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

    public void heartBeatHandler(ChannelHandlerContext ctx, RiceRemoteContext request) {
        logger.info("Heartbeat probe received from scheduler {}", RemoteHelper.parseChannelRemoteAddr(ctx.channel()));
        SchedulerHeartBeatBody body = SchedulerHeartBeatBody.decode(request.getBody(),SchedulerHeartBeatBody.class);
        body.getProcessorDetailList().forEach(pd->{
            processorManager.putProcessorStatus(pd.getAddress(),pd.getLatestActiveTime());
        });
    }

    public RiceRemoteContext pullTasks(ChannelHandlerContext ctx,
        RiceRemoteContext request) throws RemotingCommandException {
        if (this.riceController.isMaster()) {
            SchedulerPullTaskChangeRequestHeader header = (SchedulerPullTaskChangeRequestHeader) request.decodeCommandCustomHeader(SchedulerPullTaskChangeRequestHeader.class);
            List<TaskChangeRecord> changes = riceController.getRepository().getRiceTaskChangeRecordDao().getChanges(header.getTaskCode(), header.getLastTaskChangeTimestamp());
            if (changes != null && changes.size() > 0) {
                RiceRemoteContext responseCommand = RiceRemoteContext.createResponseCommand(null);
                responseCommand.setCode(ResponseCode.SUCCESS);
                SchedulerPullTaskChangeResponseBody body = new SchedulerPullTaskChangeResponseBody();
                body.setTaskChangeRecordList(changes);
                responseCommand.setBody(body.encode());
                return responseCommand;

            } else {
                // 放到队列中
                PullRequest pullRequest = new PullRequest(request, ctx.channel(), 5 * 1000, System.currentTimeMillis(), header.getLastTaskChangeTimestamp());
                riceController.getPullTaskRequestHoldService().suspendPullRequest(header.getTaskCode(), pullRequest);
            }
        } else {
            logger.info("The current controller is not a master node, so it does not process long polling requests");
        }
        return null;
    }

}
