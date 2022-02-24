package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.repository.api.dao.RiceTaskChangeRecordDao;

import java.util.List;

public class RiceTaskChangeRecordDaoImpl implements RiceTaskChangeRecordDao {
    @Override
    public long getLatestRecord(String taskCode) {
        return 0;
    }

    @Override
    public List<TaskChangeRecord> getChanges(String taskCode, Long startTime) {
        return null;
    }

    @Override
    public void insert(List<TaskChangeRecord> taskChangeRecords) {

    }
}
