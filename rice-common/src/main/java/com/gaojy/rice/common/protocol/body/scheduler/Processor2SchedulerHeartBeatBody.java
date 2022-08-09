package com.gaojy.rice.common.protocol.body.scheduler;

import com.gaojy.rice.common.protocol.RemotingSerializable;
import com.gaojy.rice.common.protocol.header.scheduler.TaskInvokeRequestHeader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName Processor2SchedulerHeartBeatBody.java
 * @Description 处理器会返回发送失败的任务结果
 * @createTime 2022/08/04 16:05:00
 */
public class Processor2SchedulerHeartBeatBody extends RemotingSerializable {
    Map<TaskInvokeRequestHeader, TaskInvokerResponseBody> sendFailedTasks = new HashMap<>();

    public Map<TaskInvokeRequestHeader, TaskInvokerResponseBody> getSendFailedTasks() {
        return sendFailedTasks;
    }

    public void setSendFailedTasks(
        Map<TaskInvokeRequestHeader, TaskInvokerResponseBody> sendFailedTasks) {
        this.sendFailedTasks = sendFailedTasks;
    }
}
