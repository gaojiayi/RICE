package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.common.exception.RepositoryException;
import com.gaojy.rice.repository.api.dao.TaskInstanceInfoDao;
import com.gaojy.rice.repository.mysql.DataSourceFactory;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskInstanceInfoDaoImpl implements TaskInstanceInfoDao {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.REPOSITORY_LOGGER_NAME);

    private final DataSource dataSource = DataSourceFactory.getDataSource();

    @Override
    public TaskInstanceInfo getInstance(Long id) {
        QueryRunner qr = new QueryRunner(dataSource);
        String sql = "select * from task_instance_info where id=" + id;
        try {
            TaskInstanceInfo instanceInfo = qr.query(sql, new BeanHandler<TaskInstanceInfo>(TaskInstanceInfo.class,
                    new BasicRowProcessor(new GenerousBeanProcessor())));
            return instanceInfo;
        } catch (SQLException e) {
            log.error("get task instance exception" + e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<TaskInstanceInfo> getInstances(String type, Long parentInstanceId) {
        return null;
    }

    @Override
    public Long createTaskInstance(TaskInstanceInfo instanceInfo) {
        QueryRunner qr = new QueryRunner(dataSource);
        String sql = "insert into  task_instance_info " +
            "(task_code,instance_params,parent_instance_id,actual_trigger_time,expected_trigger_time,retry_times," +
            "task_tracker_address,type,result,finished_time,create_time,status)" +
            " values(?,?,?,?,?,?,?,?,?,?,?,?) ";
        try {
            Long id = qr.insert(sql, new ScalarHandler<>(),
                instanceInfo.getTaskCode(),
                instanceInfo.getInstanceParams(),
                instanceInfo.getParentInstanceId(),
                instanceInfo.getActualTriggerTime(),
                instanceInfo.getExpectedTriggerTime(),
                instanceInfo.getRetryTimes(),
                instanceInfo.getTaskTrackerAddress(),
                instanceInfo.getType(),
                instanceInfo.getResult(),
                instanceInfo.getFinishedTime(),
                instanceInfo.getCreateTime(),
                instanceInfo.getStatus()
            );
            return id;
        } catch (SQLException e) {
            log.error("create task instance exception" + e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public void updateTaskInstance(TaskInstanceInfo instanceInfo) {
        QueryRunner qr = new QueryRunner(dataSource);
        String sql = "update task_instance_info set " +
            "task_code = ?, " +
            "instance_params = ?, " +
            "parent_instance_id = ?, " +
            "actual_trigger_time = ?, " +
            "retry_times = ?, " +
            "task_tracker_address = ?, " +
            "type = ?, " +
            "result = ?, " +
            "finished_time = ?, " +
            "create_time = ?, " +
            "status = ? " +
            "where id = ?";
        try {
            qr.update(sql,
                instanceInfo.getTaskCode(),
                instanceInfo.getInstanceParams(),
                instanceInfo.getParentInstanceId(),
                instanceInfo.getActualTriggerTime(),
                instanceInfo.getRetryTimes(),
                instanceInfo.getTaskTrackerAddress(),
                instanceInfo.getType(),
                instanceInfo.getResult(),
                instanceInfo.getFinishedTime(),
                instanceInfo.getCreateTime(),
                instanceInfo.getStatus(),
                instanceInfo.getId()
            );
        } catch (SQLException e) {
            log.error("update task instance exception" + e);
            throw new RepositoryException(e);
        }

    }
}
