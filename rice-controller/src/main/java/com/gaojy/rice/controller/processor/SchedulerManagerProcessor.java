package com.gaojy.rice.controller.processor;

import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author gaojy
 * @ClassName SchedulerManagerProcessor.java
 * @Description 1 处理心跳(包含处理器上报)  2 调度器拉拉取任务  3 调度器注册
 * @createTime 2022/01/23 19:27:00
 */
public class SchedulerManagerProcessor implements RiceRequestProcessor {

    private final ReentrantLock heart_beat_lock = new ReentrantLock();

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {
        switch (request.getCode()) {
            case RequestCode.SCHEDULER_REGISTER:
                return null;
            case RequestCode.SCHEDULER_HEART_BEAT:
                return null;
            case RequestCode.SCHEDULER_PULL_TASK:
                return null;
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
    public synchronized RiceRemoteContext registerScheduler(ChannelHandlerContext ctx, RiceRemoteContext request) {

        return null;

    }

    public synchronized RiceRemoteContext crashDownScheduler() {
        return null;
    }

    public void heartBeatHandler() {
        heart_beat_lock.lock();
        try {

        } catch (Exception e) {

        } finally {
            heart_beat_lock.unlock();
        }
    }

}
