package com.gaojy.rice.dispatcher.common;

import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.constants.ResponseCode;
import com.gaojy.rice.common.exception.DispatcherException;
import com.gaojy.rice.common.exception.RemotingConnectException;
import com.gaojy.rice.common.exception.RemotingSendRequestException;
import com.gaojy.rice.common.exception.RemotingTimeoutException;
import com.gaojy.rice.common.exception.RemotingTooMuchRequestException;
import com.gaojy.rice.common.protocol.header.scheduler.SchedulerHeartBeatHeader;
import com.gaojy.rice.common.protocol.header.scheduler.SchedulerPullTaskChangeRequestHeader;
import com.gaojy.rice.common.protocol.header.scheduler.SchedulerRegisterRequestHeader;
import com.gaojy.rice.dispatcher.RiceDispatchScheduler;
import com.gaojy.rice.dispatcher.longpolling.PullCallback;
import com.gaojy.rice.dispatcher.longpolling.PullRequest;
import com.gaojy.rice.dispatcher.longpolling.PullResult;
import com.gaojy.rice.remote.InvokeCallback;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.ResponseFuture;
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

    // 第一次拉取 重新分配任务  异步调用
    public void pullTask(final PullRequest pullRequest,
        final PullCallback pullCallback) throws InterruptedException, TimeoutException, RemotingConnectException,
        RemotingSendRequestException, RemotingTimeoutException, RemotingTooMuchRequestException {
        String addr = this.riceDispatchScheduler.getElectionClient().getMasterController();
        SchedulerPullTaskChangeRequestHeader header = new SchedulerPullTaskChangeRequestHeader();
        header.setLastTaskChangeTimestamp(pullRequest.getLastTaskChangeTimestamp());
        RiceRemoteContext requestCommand = RiceRemoteContext.createRequestCommand(RequestCode.SCHEDULER_PULL_TASK, header);
        int timeoutMillis = 1000 * 3;
        this.transportClient.invokeAsync(addr, requestCommand, timeoutMillis, new InvokeCallback() {
            @Override
            public void operationComplete(ResponseFuture responseFuture) {
                RiceRemoteContext response = responseFuture.getResponseCommand();
                if (response != null) {
                    try {
                        // 将响应数据解析成PullResult
                        PullResult pullResult = DispatcherAPIWrapper.this.processPullResponse(response);
                        assert pullResult != null;
                        // 对PullResult处理
                        pullCallback.onSuccess(pullResult);
                    } catch (Exception e) {
                        pullCallback.onException(e);
                    }
                } else {
                    if (!responseFuture.isSendRequestOK()) {
                        pullCallback.onException(new DispatcherException("send request failed to " + addr + ". Request: " + requestCommand, responseFuture.getCause()));
                    } else if (responseFuture.isTimeout()) {
                        pullCallback.onException(new DispatcherException("wait response from " + addr + " timeout :" + responseFuture.getTimeoutMillis() + "ms" + ". Request: " + requestCommand,
                            responseFuture.getCause()));
                    } else {
                        pullCallback.onException(new DispatcherException("unknown reason. addr: " + addr + ", timeoutMillis: " + timeoutMillis + ". Request: " + requestCommand, responseFuture.getCause()));
                    }
                }
            }
        });

    }

    private PullResult processPullResponse(RiceRemoteContext response) {
        return null;
    }

    //
    public void heartBeatToController() throws InterruptedException, TimeoutException,
        RemotingConnectException, RemotingSendRequestException,
        RemotingTimeoutException, RemotingTooMuchRequestException {
        String mainController = this.riceDispatchScheduler.getElectionClient().getMasterController();
        SchedulerHeartBeatHeader header = new SchedulerHeartBeatHeader(); //后续可以添加一些系统指标
        RiceRemoteContext command = RiceRemoteContext.createRequestCommand(RequestCode.SCHEDULER_HEART_BEAT, header);
        this.transportClient.invokeOneWay(mainController, command, 1000 * 3);
    }

    // 调度器第一次启动或者发生控制器重新选举的时候调用   控制器保存channel
    public Boolean registerScheduler() throws InterruptedException, TimeoutException,
        RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        String mainController = this.riceDispatchScheduler.getElectionClient().getMasterController();
        RiceRemoteContext request = RiceRemoteContext.createRequestCommand(RequestCode.SCHEDULER_REGISTER, new SchedulerRegisterRequestHeader());
        RiceRemoteContext response = this.transportClient.invokeSync(mainController, request, 1000 * 3);
        if (response != null && response.getCode() == ResponseCode.SUCCESS) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }

    //同步调用
    public void invokeTask() {

    }

    public void heartBeatToProcessor() throws InterruptedException, TimeoutException {

    }

}
