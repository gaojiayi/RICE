package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.repository.api.dao.TaskInstanceInfoDao;
import java.util.List;

public class TaskInstanceInfoDaoImpl implements TaskInstanceInfoDao {

    @Override
    public TaskInstanceInfo getInstance(Long id) {
        return null;
    }

    @Override public List<TaskInstanceInfo> getInstances(String type, Long parentInstanceId) {
        return null;
    }

    @Override
    public Long createTaskInstance(TaskInstanceInfo instanceInfo) {
        return null;
    }

    @Override
    public void updateTaskInstance(TaskInstanceInfo instanceInfo) {

    }
}
