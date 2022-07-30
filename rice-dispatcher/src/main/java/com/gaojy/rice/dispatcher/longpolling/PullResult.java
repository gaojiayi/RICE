package com.gaojy.rice.dispatcher.longpolling;

import com.gaojy.rice.common.entity.TaskChangeRecord;
import java.util.List;

/**
 * @author gaojy
 * @ClassName PullResult.java
 * @Description 
 * @createTime 2022/02/09 14:47:00
 */
public class PullResult {
    private List<TaskChangeRecord> taskChangeRecordList;
    private Long recodeMaxTimeStamp = -1L;

    public List<TaskChangeRecord> getTaskChangeRecordList() {
        return taskChangeRecordList;
    }

    public void setTaskChangeRecordList(List<TaskChangeRecord> taskChangeRecordList) {
        this.taskChangeRecordList = taskChangeRecordList;
    }

    public Long getRecodeMaxTimeStamp() {
        return recodeMaxTimeStamp;
    }

    public void setRecodeMaxTimeStamp(Long recodeMaxTimeStamp) {
        this.recodeMaxTimeStamp = recodeMaxTimeStamp;
    }

    @Override
    public String toString() {
        return "PullResult{" +
            "taskChangeRecordList=" + taskChangeRecordList +
            ", recodeMaxTimeStamp=" + recodeMaxTimeStamp +
            '}';
    }
}
