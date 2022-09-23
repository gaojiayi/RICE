package com.gaojy.rice.repository.api.dao;

import com.gaojy.rice.common.entity.RiceTaskInfo;
import java.util.List;

/**
 * @author gaojy
 * @ClassName RiceTaskInfoDao.java
 * @Description
 * @createTime 2022/01/17 17:09:00
 */
public interface RiceTaskInfoDao {

    List<RiceTaskInfo> getInfoByCodes(List<String> taskCodes);

    public void addTask(RiceTaskInfo riceTaskInfo);

    public void updateTask(RiceTaskInfo riceTaskInfo);

    public void taskStatusChange(String taskCode,int status);

    public List<String> getAllValidTaskCode();

}
