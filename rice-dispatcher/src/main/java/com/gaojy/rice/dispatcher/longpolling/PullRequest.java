package com.gaojy.rice.dispatcher.longpolling;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author gaojy
 * @ClassName PullRequest.java
 * @Description 
 * @createTime 2022/02/09 14:08:00
 */
public class PullRequest {

    private  volatile Long lastTaskChangeTimestamp;
    private String taskCode;

    public Long getLastTaskChangeTimestamp() {
        return lastTaskChangeTimestamp;
    }

    public void setLastTaskChangeTimestamp(Long lastTaskChangeTimestamp) {
        this.lastTaskChangeTimestamp = lastTaskChangeTimestamp;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public PullRequest(Long lastTaskChangeTimestamp, String taskCode) {
        this.lastTaskChangeTimestamp = lastTaskChangeTimestamp;
        this.taskCode = taskCode;
    }
}
