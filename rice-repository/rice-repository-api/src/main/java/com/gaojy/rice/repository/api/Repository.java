package com.gaojy.rice.repository.api;

import com.gaojy.rice.common.extension.SPI;
import com.gaojy.rice.repository.api.task.RiceTaskChangeRecordDao;

/**
 * @author gaojy
 * @ClassName Repository.java
 * @Description TODO
 * @createTime 2022/01/17 17:11:00
 */
@SPI
public interface Repository {

    public void connect();

    public void close();

    public RiceTaskChangeRecordDao getRiceTaskChangeRecordDao();
}
