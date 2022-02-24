package com.gaojy.rice.repository.api.dao;

import com.gaojy.rice.common.entity.TaskChangeRecord;
import java.util.List;

/**
 * @author gaojy
 * @ClassName RiceTaskChangeRecordDao.java
 * @Description TODO
 * @createTime 2022/01/17 18:38:00
 */
public interface RiceTaskChangeRecordDao {

    public long getLatestRecord(String taskCode);

    public List<TaskChangeRecord> getChanges(String taskCode, Long startTime);

    public void  insert(List<TaskChangeRecord> taskChangeRecords);
}
