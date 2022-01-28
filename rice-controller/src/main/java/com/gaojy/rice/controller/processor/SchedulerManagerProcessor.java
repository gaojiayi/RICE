package com.gaojy.rice.controller.processor;

import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.controller.RiceController;
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

    private final RiceController riceController;

    public SchedulerManagerProcessor(RiceController riceController) {
        this.riceController = riceController;
    }

    @Override
    public RiceRemoteContext processRequest(ChannelHandlerContext ctx, RiceRemoteContext request) throws Exception {
        switch (request.getCode()) {
            case RequestCode.SCHEDULER_REGISTER:
            case RequestCode.SCHEDULER_HEART_BEAT: // 2 step 向其他的控制器注册调度器
                return null;
            case RequestCode.SCHEDULER_PULL_TASK: // 1 step  如果是-1的时间戳，说明是新的调度器
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

    /**
     * 1 缓存scheduler channel 2 如果是主控制器 则更新对应处理器的最近活跃时间
     */
    public void heartBeatHandler() {

        heart_beat_lock.lock();
        try {

        } catch (Exception e) {

        } finally {
            heart_beat_lock.unlock();
        }
    }

    public RiceRemoteContext pullTasks(ChannelHandlerContext ctx, RiceRemoteContext request) {
        if (this.riceController.isMaster()) {

        } else {

        }
        return null;
    }

}
