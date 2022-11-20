package com.gaojy.rice.repository.api.dao;

import com.gaojy.rice.common.entity.TaskInstanceInfo;
import java.util.List;

public interface TaskInstanceInfoDao {

    public TaskInstanceInfo getInstance(Long id);

    public List<TaskInstanceInfo> getInstances(String type,Long parentInstanceId);

    public Long createTaskInstance(TaskInstanceInfo instanceInfo);

    public void updateTaskInstance(TaskInstanceInfo instanceInfo);

    public Integer getCountValidInstance();

    public List<TaskInstanceInfo>  getLatestInstance(Integer limit);

    public Integer getNumByStatus(Integer status);
}
