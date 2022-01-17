package com.gaojy.rice.repository.mysql;

import com.gaojy.rice.repository.api.Repository;
import com.gaojy.rice.repository.api.task.RiceTaskChangeRecordDao;

/**
 * @author gaojy
 * @ClassName MysqlRepository.java
 * @Description TODO
 * @createTime 2022/01/17 19:42:00
 */
public class MysqlRepository implements Repository {


    @Override
    public void connect() {

    }

    @Override
    public void close() {
        DataSourceFactory.closeDataSoure();
    }

    @Override public RiceTaskChangeRecordDao getRiceTaskChangeRecordDao() {
        return null;
    }

}
