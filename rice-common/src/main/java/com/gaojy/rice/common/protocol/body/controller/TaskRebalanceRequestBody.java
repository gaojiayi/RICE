package com.gaojy.rice.common.protocol.body.controller;

import com.gaojy.rice.common.protocol.RemotingSerializable;
import java.util.List;

/**
 * @author gaojy
 * @ClassName TaskRebalanceRequestBody.java
 * @Description 
 * @createTime 2022/02/13 16:22:00
 */
public class TaskRebalanceRequestBody extends RemotingSerializable {
    private String currentScheduler;

    private List<String> currentActiveSchedulers;

    private String changeScheduler;

    private byte schedulerOpt;

    public String getCurrentScheduler() {
        return currentScheduler;
    }

    public void setCurrentScheduler(String currentScheduler) {
        this.currentScheduler = currentScheduler;
    }

    public List<String> getCurrentActiveSchedulers() {
        return currentActiveSchedulers;
    }

    public void setCurrentActiveSchedulers(List<String> currentActiveSchedulers) {
        this.currentActiveSchedulers = currentActiveSchedulers;
    }

    public String getChangeScheduler() {
        return changeScheduler;
    }

    public void setChangeScheduler(String changeScheduler) {
        this.changeScheduler = changeScheduler;
    }

    public byte getSchedulerOpt() {
        return schedulerOpt;
    }

    public void setSchedulerOpt(byte schedulerOpt) {
        this.schedulerOpt = schedulerOpt;
    }
}
