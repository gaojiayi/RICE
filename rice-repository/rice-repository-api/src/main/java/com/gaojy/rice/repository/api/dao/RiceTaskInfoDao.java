package com.gaojy.rice.repository.api.dao;

import com.gaojy.rice.common.entity.RiceTaskInfo;
import java.util.Date;
import java.util.List;

/**
 * @author gaojy
 * @ClassName RiceTaskInfoDao.java
 * @Description
 * @createTime 2022/01/17 17:09:00
 */
public interface RiceTaskInfoDao {

    List<RiceTaskInfo> getInfoByCodes(List<String> taskCodes);

    RiceTaskInfo  getInfoByCode(String taskCode);

    public void addTask(RiceTaskInfo riceTaskInfo);

    public void updateTask(RiceTaskInfo riceTaskInfo);

    public void updateNextTriggerTime(String taskCode,Date nextTriggerTime);

    public void taskStatusChange(String taskCode,int status);

    public List<String> getAllValidTaskCode();

    public List<RiceTaskInfo> queryTasks(String taskCode,Long appId,Integer pageIndex,Integer pageSize);

    public Integer queryTasksCount(String taskCode,Long appId,Integer pageIndex,Integer pageSize);

}
