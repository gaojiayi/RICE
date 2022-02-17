package com.gaojy.rice.repository.api.dao;

import com.gaojy.rice.common.entity.TaskInstanceInfo;

public interface TaskInstanceInfoDao {

    public TaskInstanceInfo getInstance(Long id);

    public Long createTaskInstance(TaskInstanceInfo instanceInfo);

    public void updateTaskInstance(TaskInstanceInfo instanceInfo);
}
