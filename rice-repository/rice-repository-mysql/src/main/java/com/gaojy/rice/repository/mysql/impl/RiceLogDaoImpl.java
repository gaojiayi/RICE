package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.entity.RiceLog;
import com.gaojy.rice.common.exception.RepositoryException;
import com.gaojy.rice.repository.api.dao.RiceLogDao;
import com.gaojy.rice.repository.mysql.DataSourceFactory;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName RiceLogDaoImpl.java
 * @Description
 * @createTime 2022/11/08 23:06:00
 */
public class RiceLogDaoImpl implements RiceLogDao {
    private final DataSource dataSource = DataSourceFactory.getDataSource();
    private final QueryRunner qr = new QueryRunner(dataSource);
    private static final Logger LOG = LoggerFactory.getLogger(LoggerName.REPOSITORY_LOGGER_NAME);

    @Override
    public void append(RiceLog log) {
        String sql = "insert into rice_log (task_instance_id,processor_addr,scheduler_addr,message) values(?,?,?,?)";
        try {
                qr.update(sql,
                log.getTaskInstanceId(),
                log.getProcessorAddr(),
                log.getSchedulerAddr(),
                log.getMessage().getBytes(StandardCharsets.UTF_8));
        } catch (SQLException e) {
            LOG.error("append log error,log={}",log.toString());
        }

    }
}
