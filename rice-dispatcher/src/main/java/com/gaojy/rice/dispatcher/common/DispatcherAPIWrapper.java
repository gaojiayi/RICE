package com.gaojy.rice.dispatcher.common;

import com.gaojy.rice.dispatcher.RiceDispatchScheduler;
import com.gaojy.rice.remote.transport.TransportClient;

import java.util.concurrent.TimeoutException;

/**
 * @author gaojy
 * @ClassName DispatcherAPIWrapper.java
 * @Description TODO
 * @createTime 2022/02/09 22:35:00
 */
public class DispatcherAPIWrapper {
    private final RiceDispatchScheduler riceDispatchScheduler;
    private final TransportClient transportClient;

    public DispatcherAPIWrapper(RiceDispatchScheduler riceDispatchScheduler) {
        this.riceDispatchScheduler = riceDispatchScheduler;
        transportClient = this.riceDispatchScheduler.getTransportClient();
    }

    // 第一次拉取 重新分配任务
    public void pullTask() throws InterruptedException, TimeoutException {
        String mainController = this.riceDispatchScheduler.getElectionClient().getMasterController();

    }

    //
    public void heartBeatToController() throws InterruptedException, TimeoutException {
        String mainController = this.riceDispatchScheduler.getElectionClient().getMasterController();

    }

    // 调度器第一次启动或者发生控制器重新选举的时候调用   控制器保存channel
    public void registerScheduler() throws InterruptedException, TimeoutException {
        String mainController = this.riceDispatchScheduler.getElectionClient().getMasterController();

    }

    public void invokeTask() {

    }

    public void heartBeatToProcessorr() throws InterruptedException, TimeoutException {

    }


}
