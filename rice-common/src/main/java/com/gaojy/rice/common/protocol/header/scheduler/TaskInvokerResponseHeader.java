package com.gaojy.rice.common.protocol.header.scheduler;

import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;

/**
 * @author gaojy
 * @ClassName TaskInvokerResponseHeader.java
 * @Description 调度结果返回响应header
 * @createTime 2022/01/26 15:27:00
 */
public class TaskInvokerResponseHeader implements CommandCustomHeader {

    private Long taskInstanceId;

    private String taskCode;

    private Long finishTime;

    /**
     * 重试次数
     */
    private int retryTimes;

    private String taskInstanceStatus;


    public Long getTaskInstanceId() {
        return taskInstanceId;
    }

    public void setTaskInstanceId(Long taskInstanceId) {
        this.taskInstanceId = taskInstanceId;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public String getTaskInstanceStatus() {
        return taskInstanceStatus;
    }

    public void setTaskInstanceStatus(String taskInstanceStatus) {
        this.taskInstanceStatus = taskInstanceStatus;
    }

    @Override
    public void checkFields() throws RemotingCommandException {

    }
}
