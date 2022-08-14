package com.gaojy.rice.common.protocol.body.scheduler;

import com.gaojy.rice.common.protocol.RemotingSerializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author gaojy
 * @ClassName SchedulerHeartBeatBody.java
 * @Description
 * @createTime 2022/02/07 13:43:00
 */
public class SchedulerHeartBeatBody extends RemotingSerializable {
    //  调度器管理的处理器
    private List<ProcessorDetail> processorDetailList = new ArrayList<>();
    // 调度器管理的任务
    List<String> taskCodes = new ArrayList<>();

    private int CPURate;

    private int menRate;

    public static class ProcessorDetail {
        private String address;
        private Long latestActiveTime;
        private int port;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Long getLatestActiveTime() {
            return latestActiveTime;
        }

        public void setLatestActiveTime(Long latestActiveTime) {
            this.latestActiveTime = latestActiveTime;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public List<ProcessorDetail> getProcessorDetailList() {
        return processorDetailList;
    }

    public void setProcessorDetailList(
        List<ProcessorDetail> processorDetailList) {
        this.processorDetailList = processorDetailList;
    }

    public void addProcessorDetail(ProcessorDetail pd) {
        processorDetailList.add(pd);
    }

    public List<String> getTaskCodes() {
        return taskCodes;
    }

    public void setTaskCodes(List<String> taskCodes) {
        this.taskCodes = taskCodes;
    }

    public int getCPURate() {
        return CPURate;
    }

    public void setCPURate(int CPURate) {
        this.CPURate = CPURate;
    }

    public int getMenRate() {
        return menRate;
    }

    public void setMenRate(int menRate) {
        this.menRate = menRate;
    }
}
