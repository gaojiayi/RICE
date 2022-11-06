package com.gaojy.rice.common.protocol.body.processor;

import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.RemotingSerializable;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName ExportTaskResponseBody.java
 * @Description 
 * @createTime 2022/01/07 23:03:00
 */
public class ExportTaskResponseBody extends RemotingSerializable {
    private Map<String, String> taskSchedulerInfo = new HashMap<>();


    public Map<String, String> getTaskSchedulerInfo() {
        return taskSchedulerInfo;
    }

    public void setTaskSchedulerInfo(String taskCode,String dispatcherAddr) {
        taskSchedulerInfo.put(taskCode,dispatcherAddr);
    }
}
