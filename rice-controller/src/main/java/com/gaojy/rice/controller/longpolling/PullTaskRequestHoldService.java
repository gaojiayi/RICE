package com.gaojy.rice.controller.longpolling;

import com.alipay.remoting.RemotingCommand;
import com.gaojy.rice.common.BackgroundThread;
import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.exception.RemotingCommandException;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    /**
     * 存放来自调度器的任务拉取请求
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
                    this.waitForRunning(5 * 1000);
                } else {
                    this.waitForRunning(this.controller.getShortPollingTimeMills());
                }

                long beginLockTimestamp = this.systemClock.now();
                this.checkHoldRequest();
                long costTime = this.systemClock.now() - beginLockTimestamp;
                if (costTime > 5 * 1000) {
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
     * @param latestTaskUpdateTime
     * @throws
     * @description 当发生调度器宕机  任务删改  新调度器上线  ，并通过长轮询让对应的调度器获取
     */
    public void notifyTaskOccurChange(String taskCode, Long latestTaskUpdateTime) {

        ManyTaskPullRequest mpr = this.pullRequestTable.get(taskCode);
        if (mpr != null) {
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
                            responseCommand.setBody(body.encode());
                            // 获取到所有的task变更 并发送出去
                            executeRequestWhenWakeup(request.getClientChannel(), request.getRiceRemoteContext(), responseCommand);

                        } catch (Throwable e) {
                            log.error("execute request when wakeup failed.", e);
                        }
                        continue;

                    }

                    // 如果scheduler的客户端请求以后超时了   则立即返回
                    if (System.currentTimeMillis() >= (request.getSuspendTimestamp() + request.getTimeoutMillis())) {
                        try {
                            RiceRemoteContext responseCommand = RiceRemoteContext.createResponseCommand(null);
                            responseCommand.setCode(ResponseCode.SUCCESS);
                            SchedulerPullTaskChangeResponseBody body = new SchedulerPullTaskChangeResponseBody();
                            responseCommand.setBody(body.encode());
                            // 获取到所有的task变更 并发送出去
                            executeRequestWhenWakeup(request.getClientChannel(), request.getRiceRemoteContext(), responseCommand);

                        } catch (Throwable e) {
                            log.error("execute request when wakeup failed.", e);
                        }
                        continue;
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

    // 在Dispatcher中的DispatcherAPIWrapper@pullTask处理响应
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
