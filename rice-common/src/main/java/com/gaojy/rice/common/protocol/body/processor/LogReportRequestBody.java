package com.gaojy.rice.common.protocol.body.processor;

import com.gaojy.rice.common.protocol.RemotingSerializable;

/**
 * @author gaojy
 * @ClassName LogReportRequestBody.java
 * @Description 
 * @createTime 2022/07/28 15:48:00
 */
public class LogReportRequestBody extends RemotingSerializable {
    private Long taskInstanceId;
    private String logMessage;

    public Long getTaskInstanceId() {
        return taskInstanceId;
    }

    public void setTaskInstanceId(Long taskInstanceId) {
        this.taskInstanceId = taskInstanceId;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
}
