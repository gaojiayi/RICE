package com.gaojy.rice.repository.api.task;

/**
 * @author gaojy
 * @ClassName RiceTaskChangeRecordDao.java
 * @Description TODO
 * @createTime 2022/01/17 18:38:00
 */
public interface RiceTaskChangeRecordDao {

    public long getLatestRecord(String schedule);
}
