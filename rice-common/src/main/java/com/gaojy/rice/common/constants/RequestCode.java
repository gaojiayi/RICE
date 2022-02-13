package com.gaojy.rice.common.constants;

/**
 * @author gaojy
 * @ClassName RequestCode.java
 * @Description TODO
 * @createTime 2022/01/07 16:35:00
 */
public class RequestCode {

    // from scheduler
    public static final int INVOKE_PROCESSOR = 100;

    // from processor
    public static final int REGISTER_PROCESSOR = 200;

    // 处理日志上报
    public static final int LOG_REPORT = 210;

    // 调度器的任务拉取
    public static final int SCHEDULER_PULL_TASK = 300;

    //调度器的心跳
    public static final int SCHEDULER_HEART_BEAT = 310;

    //调度器的注册
    public static final int SCHEDULER_REGISTER = 320;


    // 控制器通知任务重分配
    public static final int CONTROLLER_TASK_REBALANCE = 400;

    // 控制器直接call响应的调度器创建任务
    public static final int CONTROLLER_TASK_CREATE = 401;

}
