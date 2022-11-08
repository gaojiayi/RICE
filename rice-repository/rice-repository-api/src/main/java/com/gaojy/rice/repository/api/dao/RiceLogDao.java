package com.gaojy.rice.repository.api.dao;

import com.gaojy.rice.common.entity.RiceLog;

/**
 * @author gaojy
 * @ClassName RiceLogDao.java
 * @Description
 * @createTime 2022/11/08 23:04:00
 */
public interface RiceLogDao {

    public void append(RiceLog log);
}
