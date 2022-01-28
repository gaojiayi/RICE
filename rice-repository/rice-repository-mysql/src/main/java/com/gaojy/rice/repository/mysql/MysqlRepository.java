package com.gaojy.rice.repository.mysql;

import com.gaojy.rice.repository.api.Repository;
import com.gaojy.rice.repository.api.dao.ProcessorServerInfoDao;
import com.gaojy.rice.repository.api.dao.RiceTaskChangeRecordDao;
import com.gaojy.rice.repository.api.dao.RiceTaskInfoDao;
import com.gaojy.rice.repository.mysql.impl.ProcessorServerInfoDaoImpl;
import com.gaojy.rice.repository.mysql.impl.RiceTaskInfoDaoImpl;
import javax.sql.DataSource;

/**
 * @author gaojy
 * @ClassName MysqlRepository.java
 * @Description TODO
 * @createTime 2022/01/17 19:42:00
 */
public class MysqlRepository implements Repository {

    private final ProcessorServerInfoDao processorServerInfoDao = new ProcessorServerInfoDaoImpl();
    private final RiceTaskInfoDao riceTaskInfoDao = new RiceTaskInfoDaoImpl();

    @Override
    public void connect() {
        DataSourceFactory.getConnection();
    }

    public DataSource getDataSource() {
        return DataSourceFactory.getDataSource();
    }

    @Override
    public void close() {
        DataSourceFactory.closeDataSoure();
    }

    @Override public RiceTaskChangeRecordDao getRiceTaskChangeRecordDao() {
        return null;
    }

    @Override public ProcessorServerInfoDao getProcessorServerInfoDao() {
        return processorServerInfoDao;
    }

    @Override public RiceTaskInfoDao getRiceTaskInfoDao() {
        return riceTaskInfoDao;
    }

}
