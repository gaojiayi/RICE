package com.gaojy.rice.controller.longpolling;

import com.gaojy.rice.common.BackgroundThread;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.controller.RiceController;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvm.hotspot.debugger.Address;

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
    private ConcurrentMap<String/* Scheduler Server Address */, ManyTaskPullRequest> pullRequestTable =
        new ConcurrentHashMap<String, ManyTaskPullRequest>(1024);

    private RiceController controller;

    public PullTaskRequestHoldService(RiceController controller) {
        this.controller = controller;
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
        for (String key : this.pullRequestTable.keySet()) {
            // 获取到当前调度器管理的task的更新记录的最新时间
            final long latestUpdatedTime = this.controller.getRepository().getRiceTaskChangeRecordDao()
                .getLatestRecord(key);
            try {
                // 消息到达通知
                this.notifyTaskOccurChange(key, latestUpdatedTime);
            } catch (Throwable e) {
                log.error("check hold request failed. scheduler server={}", key, e);
            }
        }

    }

    /**
     * @param scheduelerAddress
     * @param latestTaskUpdateTime
     * @throws
     * @description 当发生调度器宕机  任务增删改  新调度器上线  就会触发新的任务分配，并通过长连接让对应的调度器获取
     */
    public void notifyTaskOccurChange(String scheduelerAddress, Long latestTaskUpdateTime) {

        ManyTaskPullRequest mpr = this.pullRequestTable.get(scheduelerAddress);
        if (mpr != null) {
            List<PullRequest> requestList = mpr.cloneListAndClear();
            if (requestList != null) {
                List<PullRequest> replayList = new ArrayList<PullRequest>();
                for (PullRequest request : requestList) {
                    long newestOffset = latestTaskUpdateTime;
                    if (request.getPullFromThisOffset() < 0) { // 做一个全量的数据同步
                        // TODO
                        continue;
                    }
                    if (newestOffset <= request.getPullFromThisOffset()) { // 没有更新
                        newestOffset = this.controller.getRepository().getRiceTaskChangeRecordDao()
                            .getLatestRecord(scheduelerAddress);
                    }

                    // 存在新的任务
                    if (newestOffset > request.getPullFromThisOffset()) {

                        try {
                            //TODO 获取到所有的task变更 并发送出去

                        } catch (Throwable e) {
                            log.error("execute request when wakeup failed.", e);
                        }
                        continue;

                    }

                    // 如果scheduler的客户端请求以后超时了   则立即返回
                    if (System.currentTimeMillis() >= (request.getSuspendTimestamp() + request.getTimeoutMillis())) {
                        try {
                            //TODO 获取到所有的task变更 并发送出去
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
}
