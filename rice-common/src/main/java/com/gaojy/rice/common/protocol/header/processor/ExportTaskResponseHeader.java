package com.gaojy.rice.common.protocol.header.processor;

import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName ExportTaskResponseHeader.java
 * @Description TODO
 * @createTime 2022/01/07 23:03:00
 */
public class ExportTaskResponseHeader implements CommandCustomHeader {
    private Map<String, String> taskSchedulerInfo;

    @Override public void checkFields() throws RemotingCommandException {

    }

    public Map<String, String> getTaskSchedulerInfo() {
        return taskSchedulerInfo;
    }

    public void setTaskSchedulerInfo(Map<String, String> taskSchedulerInfo) {
        this.taskSchedulerInfo = taskSchedulerInfo;
    }
}
