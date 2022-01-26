package com.gaojy.rice.common.protocol.body.processor;

import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.RemotingSerializable;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName ExportTaskResponseBody.java
 * @Description TODO
 * @createTime 2022/01/07 23:03:00
 */
public class ExportTaskResponseBody extends RemotingSerializable {
    private Map<String, String> taskSchedulerInfo;


    public Map<String, String> getTaskSchedulerInfo() {
        return taskSchedulerInfo;
    }

    public void setTaskSchedulerInfo(Map<String, String> taskSchedulerInfo) {
        this.taskSchedulerInfo = taskSchedulerInfo;
    }
}
