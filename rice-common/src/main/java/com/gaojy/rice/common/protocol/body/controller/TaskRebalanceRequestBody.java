package com.gaojy.rice.common.protocol.body.controller;

import com.gaojy.rice.common.protocol.RemotingSerializable;
import java.util.List;

/**
 * @author gaojy
 * @ClassName TaskRebalanceRequestBody.java
 * @Description TODO
 * @createTime 2022/02/13 16:22:00
 */
public class TaskRebalanceRequestBody extends RemotingSerializable {
    private String currentScheduler;

    private List<String> currentActiveSchedulers;

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
}
