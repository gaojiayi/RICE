package com.gaojy.rice.common.protocol.body.processor;

import com.gaojy.rice.common.protocol.RemotingSerializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaojy
 * @ClassName ExportTaskRequestBody.java
 * @Description TODO
 * @createTime 2022/01/07 22:18:00
 */
public class ExportTaskRequestBody extends RemotingSerializable {

    private List<TaskDetailData> tasks = new ArrayList<>();

    public List<TaskDetailData> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDetailData> tasks) {
        this.tasks = tasks;
    }

    public void addTask(TaskDetailData taskDetailData) {
        tasks.add(taskDetailData);
    }
}
