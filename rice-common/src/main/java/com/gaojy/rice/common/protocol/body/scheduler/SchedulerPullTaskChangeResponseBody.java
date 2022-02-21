package com.gaojy.rice.common.protocol.body.scheduler;

import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.protocol.RemotingSerializable;

import java.util.List;

public class SchedulerPullTaskChangeResponseBody extends RemotingSerializable {
    private List<TaskChangeRecord> taskChangeRecordList;

    public List<TaskChangeRecord> getTaskChangeRecordList() {
        return taskChangeRecordList;
    }

    public void setTaskChangeRecordList(List<TaskChangeRecord> taskChangeRecordList) {
        this.taskChangeRecordList = taskChangeRecordList;
    }
}
