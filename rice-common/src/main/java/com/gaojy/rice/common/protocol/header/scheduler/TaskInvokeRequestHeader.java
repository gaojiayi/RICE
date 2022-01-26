package com.gaojy.rice.common.protocol.header.scheduler;

import com.gaojy.rice.common.annotation.CFNotNull;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;

/**
 * @author gaojy
 * @ClassName TaskInvokeRequestHeader.java
 * @Description TODO
 * @createTime 2022/01/26 15:22:00
 */
public class TaskInvokeRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String taskCode;

    @CFNotNull
    private String taskInstanceId;

    @CFNotNull
    private String schedulerServer;

    private String taskParameter;

    // 实例参数
    private String instanceParameter;
    // method
    private String methodName;

    private int maxRetryTimes;

    private String appName;

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskInstanceId() {
        return taskInstanceId;
    }

    public void setTaskInstanceId(String taskInstanceId) {
        this.taskInstanceId = taskInstanceId;
    }

    public String getSchedulerServer() {
        return schedulerServer;
    }

    public void setSchedulerServer(String schedulerServer) {
        this.schedulerServer = schedulerServer;
    }

    public String getTaskParameter() {
        return taskParameter;
    }

    public void setTaskParameter(String taskParameter) {
        this.taskParameter = taskParameter;
    }

    public String getInstanceParameter() {
        return instanceParameter;
    }

    public void setInstanceParameter(String instanceParameter) {
        this.instanceParameter = instanceParameter;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public void checkFields() throws RemotingCommandException {

    }
}
