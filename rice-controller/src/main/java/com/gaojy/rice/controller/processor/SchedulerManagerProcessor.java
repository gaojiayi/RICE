package com.gaojy.rice.controller.processor;

import com.gaojy.rice.common.constants.RequestCode;
import com.gaojy.rice.common.protocol.body.scheduler.SchedulerHeartBeatBody;
import com.gaojy.rice.controller.RiceController;
import com.gaojy.rice.controller.maintain.SchedulerManager;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.RiceRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author gaojy
 * @ClassName SchedulerManagerProcessor.java
 * @Description 1 处理心跳(包含处理器上报)  2 调度器拉拉取任务  3 调度器向主控制器注册(触发任务再分配)
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
        // 只有主控制器执行
        switch (request.getCode()) {
            case RequestCode.SCHEDULER_REGISTER: //  增加channel
            case RequestCode.SCHEDULER_HEART_BEAT: // 2 step 向其他的控制器注册调度器
                this.heartBeatHandler(ctx, request);
                return null;
            case RequestCode.SCHEDULER_PULL_TASK: // 1 step  如果是-1的时间戳，说明是新的调度器  重新分配任务
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
        if (this.riceController.isMaster()) {
            // 任务再分配 并生成任务分配记录
        }
        return null;

    }

    public synchronized RiceRemoteContext crashDownScheduler() {
        // 当发送调度器宕机  可再次分配任务
        return null;
    }

    /**
     * 1 缓存scheduler channel 2 如果是主控制器 则更新对应处理器的最近活跃时间
     */
    public void heartBeatHandler(ChannelHandlerContext ctx, RiceRemoteContext request) {
        // 后续可以使用布隆过滤器优化

        // 缓存scheduler channel
        SchedulerManager.getManager().addSchedulerIfAbsent(ctx.channel());

        // 如果是主控制器 则更新对应处理器的最近活跃时间
        if (this.riceController.isMaster()) {
            heart_beat_lock.lock();
            try {
                //解析数据
                SchedulerHeartBeatBody beatBody = SchedulerHeartBeatBody
                    .decode(request.getBody(), SchedulerHeartBeatBody.class);
                // 批量更新

            } catch (Exception e) {

            } finally {
                heart_beat_lock.unlock();
            }
        }

    }

    public RiceRemoteContext pullTasks(ChannelHandlerContext ctx, RiceRemoteContext request) {
        if (this.riceController.isMaster()) {

        } else {

        }
        return null;
    }

}
