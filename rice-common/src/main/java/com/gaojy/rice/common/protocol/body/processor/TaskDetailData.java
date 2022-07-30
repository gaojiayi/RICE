package com.gaojy.rice.common.protocol.body.processor;

import com.gaojy.rice.common.constants.TaskType;
import java.util.Objects;

/**
 * @author gaojy
 * @ClassName TaskDetailData.java
 * @Description
 * @createTime 2022/01/07 20:50:00
 */
public class TaskDetailData {

    private String taskCode;

    private String taskName;

    private String className;

    private boolean logEnable;

    public TaskDetailData(String taskCode, String taskName, String className, boolean logEnable) {
        this.taskCode = taskCode;
        this.taskName = taskName;
        this.className = className;
        this.logEnable = logEnable;
    }

    public boolean isLogEnable() {
        return logEnable;
    }


    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TaskDetailData data = (TaskDetailData) o;
        return taskCode.equals(data.taskCode) && className.equals(data.className) && logEnable == data.logEnable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskCode, className, logEnable);
    }
}
