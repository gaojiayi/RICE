package com.gaojy.rice.dispatcher.longpolling;

import com.gaojy.rice.common.BackgroundThread;
import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.dispatcher.RiceDispatchScheduler;

import com.gaojy.rice.dispatcher.scheduler.SchedulerManager;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.gaojy.rice.dispatcher.common.DispatcherAPIWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName PullTaskService.java
 * @Description
 * @createTime 2022/02/09 13:18:00
 */
public class PullTaskService extends BackgroundThread {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.DISPATCHER_LOGGER_NAME);

    private final DispatcherAPIWrapper dispatcherAPIWrapper;

    private final LinkedBlockingQueue<PullRequest> pullRequestQueue = new LinkedBlockingQueue<PullRequest>();

    private final ScheduledExecutorService scheduledExecutorService = Executors
        .newSingleThreadScheduledExecutor(new RiceThreadFactory("PullTaskChangeServiceScheduledThread"));

    private final SchedulerManager schedulerManager;

    public PullTaskService(DispatcherAPIWrapper dispatcherAPIWrapper, SchedulerManager schedulerManager) {
        this.dispatcherAPIWrapper = dispatcherAPIWrapper;
        this.schedulerManager = schedulerManager;
    }

    @Override
    public String getServiceName() {
        return PullTaskService.class.getSimpleName();
    }

    @Override
    public void run() {
        log.info(this.getServiceName() + " service started");

        while (!this.isStopped()) {
            try {
                PullRequest pullRequest = this.pullRequestQueue.take();
                if (pullRequest != null) {
                    this.pullTaskChange(pullRequest);
                }
            } catch (InterruptedException e) {
            } catch (Exception e) {
                log.error("Pull task Service Run Method exception", e);
            }
        }

        log.info(this.getServiceName() + " service end");
    }

    public void pullTaskChange(PullRequest request) {
        try {
            final PullCallback pullCallback = new PullCallback() {
                @Override
                public void onSuccess(PullResult pullResult) {
                    try {
                        // 构建任务调度实例
                        if (pullResult.getTaskChangeRecordList() != null && pullResult.getTaskChangeRecordList().size() > 0) {
                            pullResult.getTaskChangeRecordList().forEach(record -> {
                                PullTaskService.this.schedulerManager.onChange(record);
                            });
                        }
                        // 将最新的一次更新记录时间戳赋值给 PullRequest
                        request.setLastTaskChangeTimestamp(pullResult.getRecodeMaxTimeStamp());
                        PullTaskService.this.executePullRequestImmediately(request);
                    } catch (Exception e) {
                        log.error("Failed to process pullresult, taskcode:{},pullResult:{},error:{}", request.getTaskCode(), pullResult, e);
                        PullTaskService.this.executePullRequestLater(request, 1000);
                    }

                }

                @Override
                public void onException(Throwable e) {
                    log.error("Failed to process pullresult, taskcode:{},error:{}", request.getTaskCode(), e);
                    PullTaskService.this.executePullRequestLater(request, 1000);

                }
            };
            // 异步向主控制器发送长轮询
            //String mainController = this.riceDispatchScheduler.getElectionClient().getMasterController();

            //this.riceDispatchScheduler.getTransportClient().invokeAsync();

            // 如果发生异常  比如请求异常  选举异常   则放到延迟队列中
            dispatcherAPIWrapper.pullTask(request, pullCallback);
        } catch (InterruptedException | TimeoutException | RemotingConnectException
            | RemotingSendRequestException | RemotingTimeoutException | RemotingTooMuchRequestException e) {
            log.error("pull task change exception,will try", e);
            this.executePullRequestLater(request, 1000);
        }
    }

    public void executePullRequestLater(final PullRequest pullRequest, final long timeDelay) {
        this.scheduledExecutorService.schedule(new Runnable() {

            @Override
            public void run() {
                PullTaskService.this.executePullRequestImmediately(pullRequest);
            }
        }, timeDelay, TimeUnit.MILLISECONDS);
    }

    public void executePullRequestImmediately(final PullRequest pullRequest) {
        try {
            this.pullRequestQueue.put(pullRequest);
        } catch (InterruptedException e) {
            log.error("executePullRequestImmediately pullRequestQueue.put", e);
        }
    }

}
