package com.gaojy.rice.processor.api;

import io.netty.channel.Channel;

/**
 * @author gaojy
 * @ClassName TaskContext.java
 * @Description 
 * @createTime 2022/01/02 13:57:00
 */
public class TaskContext {
    private Long taskInstanceId;
    private String taskCode;
    private Channel channel;
    private String parameter;

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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
