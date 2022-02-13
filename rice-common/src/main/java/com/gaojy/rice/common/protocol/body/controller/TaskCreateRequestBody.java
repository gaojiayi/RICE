package com.gaojy.rice.common.protocol.body.controller;

import com.gaojy.rice.common.entity.ProcessorServerInfo;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.common.protocol.RemotingSerializable;
import java.util.List;

/**
 * @author gaojy
 * @ClassName TaskCreateRequestBody.java
 * @Description TODO
 * @createTime 2022/02/13 17:02:00
 */
public class TaskCreateRequestBody extends RemotingSerializable {
    private RiceTaskInfo taskInfo;
    private List<ProcessorServerInfo> processores;

    public List<ProcessorServerInfo> getProcessores() {
        return processores;
    }

    public void setProcessores(List<ProcessorServerInfo> processores) {
        this.processores = processores;
    }

    public RiceTaskInfo getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(RiceTaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }
}
