package com.gaojy.rice.dispatcher.longpolling;

import com.gaojy.rice.common.BackgroundThread;
import com.gaojy.rice.common.RiceThreadFactory;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.dispatcher.RiceDispatchScheduler;

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
 * @Description TODO
 * @createTime 2022/02/09 13:18:00
 */
public class PullTaskService extends BackgroundThread {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.DISPATCHER_LOGGER_NAME);

    private final DispatcherAPIWrapper dispatcherAPIWrapper;

    private final LinkedBlockingQueue<PullRequest> pullRequestQueue = new LinkedBlockingQueue<PullRequest>();

    private final ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor(new RiceThreadFactory("PullTaskChangeServiceScheduledThread"));


    public PullTaskService(DispatcherAPIWrapper dispatcherAPIWrapper) {
        this.dispatcherAPIWrapper = dispatcherAPIWrapper;
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
            // 异步向主控制器发送长轮询
            //String mainController = this.riceDispatchScheduler.getElectionClient().getMasterController();

            //this.riceDispatchScheduler.getTransportClient().invokeAsync();

            // 如果发生异常  比如请求异常  选举异常   则放到延迟队列中
            dispatcherAPIWrapper.pullTask();
        } catch (InterruptedException | TimeoutException e) {
            log.error("pull task change exception,will try,exception={}", e);
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
