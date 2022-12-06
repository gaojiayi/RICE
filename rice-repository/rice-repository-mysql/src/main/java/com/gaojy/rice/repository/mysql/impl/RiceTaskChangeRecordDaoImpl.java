package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.common.exception.RepositoryException;
import com.gaojy.rice.repository.api.dao.RiceTaskChangeRecordDao;
import com.gaojy.rice.repository.mysql.DataSourceFactory;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RiceTaskChangeRecordDaoImpl implements RiceTaskChangeRecordDao {
    private static final Logger LOG = LoggerFactory.getLogger(LoggerName.REPOSITORY_LOGGER_NAME);
    private final DataSource dataSource = DataSourceFactory.getDataSource();
    private String baseSql = "select * from task_change_record";
    QueryRunner queryRunner = new QueryRunner(dataSource);

    @Override
    public long getLatestRecord(String taskCode) {

        String sql = "select MAX(create_time) from task_change_record where task_code = ?";
        try {
            Date createTime = queryRunner.query(sql, new ScalarHandler<Timestamp>(), taskCode);
            if (createTime != null) {
                return createTime.getTime();
            }
            return 0L;
        } catch (SQLException e) {
            LOG.error("getLatestRecord error", e);
            throw new RepositoryException("getLatestRecord error");
        }
    }

    @Override
    public List<TaskChangeRecord> getChanges(String taskCode, Long startTime) {
        String sql = "select * from task_change_record where task_code = ? and create_time >= ? ";

        try {
            List<TaskChangeRecord> rets = queryRunner.query(sql, new BeanListHandler<TaskChangeRecord>(TaskChangeRecord.class,
                new BasicRowProcessor(new GenerousBeanProcessor())), taskCode, new Timestamp(startTime));
            return rets;
        } catch (SQLException e) {
            LOG.error("getChanges SQLException", e);
            throw new RepositoryException("getChanges SQLException", e);
        }
    }

    @Override
    public void insert(List<TaskChangeRecord> taskChangeRecords) {

        String sql = "insert into  task_change_record (task_code,opt_type,create_time) " +
            " values (?,?,?)";

        Object[][] params = new Object[taskChangeRecords.size()][3];
        for (int i = 0; i < taskChangeRecords.size(); i++) {
            TaskChangeRecord taskChangeRecord = taskChangeRecords.get(i);
            params[i][0] = taskChangeRecord.getTaskCode();
            params[i][1] = taskChangeRecord.getOptType();
            params[i][2] = new Timestamp(taskChangeRecord.getCreateTime().getTime());
        }
        try {
            queryRunner.batch(sql, params);
        } catch (SQLException e) {
            LOG.error("insert taskChangeRecords SQLException", e);
            throw new RepositoryException("insert taskChangeRecords SQLException", e);
        }

    }

    @Override
    public void insert(TaskChangeRecord record) {
        List<TaskChangeRecord> records = new ArrayList<>();
        records.add(record);
        insert(records);
    }
}
