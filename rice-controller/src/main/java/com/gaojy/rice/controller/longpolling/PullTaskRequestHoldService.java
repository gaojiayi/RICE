package com.gaojy.rice.controller.longpolling;

import com.gaojy.rice.common.BackgroundThread;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.protocol.body.scheduler.SchedulerPullTaskChangeResponseBody;
import com.gaojy.rice.controller.RiceController;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName PullTaskRequestHoldService.java
 * @Description 处理调度器长轮询的任务拉取请求
 * @createTime 2022/01/17 14:46:00
 */
public class PullTaskRequestHoldService extends BackgroundThread {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.CONTROLLER_LOGGER_NAME);
    private final SystemClock systemClock = new SystemClock();
    public static final long LONG_POLLING_INTERVAL = 6 * 1000L;
    /**
     * 存放来自调度器的任务拉取请求
     * TODO: 随着任务数越来越多  单线程扫描会越来越耗时 后期多线程取数据
     */
    private ConcurrentMap<String/* taskCode */, ManyTaskPullRequest> pullRequestTable =
        new ConcurrentHashMap<String, ManyTaskPullRequest>(1024);

    private RiceController controller;

    public PullTaskRequestHoldService(RiceController controller) {
        this.controller = controller;
    }

    public void suspendPullRequest(final String taskCode, final PullRequest pullRequest) {
        ManyTaskPullRequest mpr = this.pullRequestTable.get(taskCode);
        if (null == mpr) {
            mpr = new ManyTaskPullRequest();
            ManyTaskPullRequest prev = this.pullRequestTable.putIfAbsent(taskCode, mpr);
            if (prev != null) {
                mpr = prev;
            }
        }

        mpr.addPullRequest(pullRequest);
    }

    @Override
    public String getServiceName() {
        return PullTaskRequestHoldService.class.getSimpleName();
    }

    @Override
    public void run() {
        log.info("{} service started", this.getServiceName());
        while (!this.isStopped()) {
            try {
                if (this.controller.isLongPollingEnable()) {
                    this.waitForRunning(LONG_POLLING_INTERVAL);
                } else {
                    this.waitForRunning(this.controller.getShortPollingTimeMills());
                }

                long beginLockTimestamp = this.systemClock.now();
                this.checkHoldRequest();
                long costTime = this.systemClock.now() - beginLockTimestamp;
                if (costTime > LONG_POLLING_INTERVAL) {
                    log.info("[NOTIFYME] check hold request cost {} ms.", costTime);
                }
            } catch (Throwable e) {
                log.warn(this.getServiceName() + " service has exception. ", e);
            }
        }

        log.info("{} service end", this.getServiceName());
    }

    private void checkHoldRequest() {
        for (String taskCode : this.pullRequestTable.keySet()) {
            this.controller.getSchedulerManagerExecutor().execute(() -> {
                // 获取到当前调度器管理的task的更新记录的最新时间
                final long latestUpdatedTime = PullTaskRequestHoldService.this.controller.getRepository().getRiceTaskChangeRecordDao()
                    .getLatestRecord(taskCode);
                try {
                    // 消息到达通知
                    PullTaskRequestHoldService.this.notifyTaskOccurChange(taskCode, latestUpdatedTime);

                } catch (Throwable e) {
                    log.error("check hold request failed. taskCode={}", taskCode, e);
                }
            });

        }

    }

    /**
     * @param taskCode
     * @param latestTaskUpdateTime 当前taskcode 发生change的最新时间
     * @throws
     * @description 任务删改 并通过长轮询让对应的调度器获取
     */
    public void notifyTaskOccurChange(String taskCode, Long latestTaskUpdateTime) {

        ManyTaskPullRequest mpr = this.pullRequestTable.get(taskCode);
        if (mpr != null) {
            // 加锁 clone  +  clear ， 后续不用在pullRequestTable执行remove操作了
            List<PullRequest> requestList = mpr.cloneListAndClear();
            if (requestList != null) {
                List<PullRequest> replayList = new ArrayList<PullRequest>();
                for (PullRequest request : requestList) {
                    long newestOffset = latestTaskUpdateTime;
                    if (request.getPullFromThisOffset() < 0) {
                        log.warn("An invalid request");
                        continue;
                    }
                    if (newestOffset <= request.getPullFromThisOffset()) { // 没有更新
                        // 再次同步一下newestOffset
                        newestOffset = this.controller.getRepository().getRiceTaskChangeRecordDao()
                            .getLatestRecord(taskCode);
                    }

                    // 存在新的任务
                    if (newestOffset > request.getPullFromThisOffset()) {
                        try {
                            RiceRemoteContext responseCommand = RiceRemoteContext.createResponseCommand(null);
                            responseCommand.setCode(ResponseCode.SUCCESS);
                            SchedulerPullTaskChangeResponseBody body = new SchedulerPullTaskChangeResponseBody();
                            List<TaskChangeRecord> changes = this.controller.getRepository().getRiceTaskChangeRecordDao().getChanges(taskCode, request.getPullFromThisOffset());
                            body.setTaskChangeRecordList(changes);
                            body.setLatestOffset(newestOffset);
                            responseCommand.setBody(body.encode());
                            // 获取到所有的task变更 并发送出去
                            executeRequestWhenWakeup(request.getClientChannel(), request.getRiceRemoteContext(), responseCommand);

                        } catch (Throwable e) {
                            log.error("execute request when wakeup failed.", e);
                        }
                        continue;

                    }

                    // 如果scheduler的客户端请求以后超时了   则立即返回
                    if (systemClock.now() >= (request.getSuspendTimestamp() + request.getTimeoutMillis())) {
                        try {
                            RiceRemoteContext responseCommand = RiceRemoteContext.createResponseCommand(null);
                            responseCommand.setCode(ResponseCode.SUCCESS);
                            SchedulerPullTaskChangeResponseBody body = new SchedulerPullTaskChangeResponseBody();
                            body.setLatestOffset(newestOffset);
                            responseCommand.setBody(body.encode());
                            // 获取到所有的task变更 并发送出去
                            executeRequestWhenWakeup(request.getClientChannel(), request.getRiceRemoteContext(), responseCommand);

                        } catch (Throwable e) {
                            log.error("execute request when wakeup failed.", e);
                        }
                        continue;
                    } else {
                        // 没有超时  重新放回到队列中
                        log.info("taskCode:{},not found changes and not timeout , reput to queue", taskCode);
                        this.suspendPullRequest(taskCode, request);
                    }

                }
            }

        }

    }

    public class SystemClock {
        public long now() {
            return System.currentTimeMillis();
        }
    }

    private void executeRequestWhenWakeup(final Channel channel, RiceRemoteContext request,
        final RiceRemoteContext response) {
        if (response != null) {
            response.setOpaque(request.getOpaque());
            response.markResponseType();
            try {
                channel.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            log.error("processRequestWrapper response to {} failed",
                                future.channel().remoteAddress(), future.cause());
                            log.error(request.toString());
                            log.error(response.toString());
                        }
                    }
                });
            } catch (Throwable e) {
                log.error("processRequestWrapper process request over, but response failed", e);
                log.error(request.toString());
                log.error(response.toString());
            }
        }

    }

}
