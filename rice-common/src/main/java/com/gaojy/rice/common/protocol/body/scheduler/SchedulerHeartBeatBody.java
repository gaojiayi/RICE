package com.gaojy.rice.common.protocol.body.scheduler;

import com.gaojy.rice.common.protocol.RemotingSerializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author gaojy
 * @ClassName SchedulerHeartBeatBody.java
 * @Description TODO
 * @createTime 2022/02/07 13:43:00
 */
public class SchedulerHeartBeatBody extends RemotingSerializable {
    List<ProcessorDetail> processorDetailList = new ArrayList<>();

    public class ProcessorDetail {
        private String address;
        private int port;
        private Set<String> taskCodes = new HashSet<>();
        private Long latestActiveTime;
    }

    public List<ProcessorDetail> getProcessorDetailList() {
        return processorDetailList;
    }

    public void setProcessorDetailList(
        List<ProcessorDetail> processorDetailList) {
        this.processorDetailList = processorDetailList;
    }
}
