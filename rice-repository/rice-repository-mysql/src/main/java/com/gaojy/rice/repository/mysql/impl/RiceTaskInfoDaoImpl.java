package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.exception.RepositoryException;
import com.gaojy.rice.repository.api.dao.RiceTaskInfoDao;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.repository.mysql.DataSourceFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.sql.DataSource;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName RiceTaskInfoDaoImpl.java
 * @Description
 * @createTime 2022/01/28 16:25:00
 */
public class RiceTaskInfoDaoImpl implements RiceTaskInfoDao {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.REPOSITORY_LOGGER_NAME);
    private final DataSource dataSource = DataSourceFactory.getDataSource();

    @Override
    public List<RiceTaskInfo> getInfoByCodes(List<String> taskCodes) {
        QueryRunner qr = new QueryRunner(dataSource);
        String condition = String.join(",", taskCodes);
        String sql = "select * from rice_task_info where task_code in ('" + condition + "') and status != 0";
        try {
            List<RiceTaskInfo> riceTaskInfos = qr.query(sql,
                new BeanListHandler<RiceTaskInfo>(RiceTaskInfo.class,
                    new BasicRowProcessor(new GenerousBeanProcessor())));
            return riceTaskInfos;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void addTask(RiceTaskInfo riceTaskInfo) {
        QueryRunner qr = new QueryRunner(dataSource);
        String sql = "insert into  rice_task_info" +
            "(task_name,task_desc,task_type,parameters,scheduler_server," +
            "schedule_type,time_expression,execute_type,task_retry_count,next_trigger_time,create_time,update_time,status)" +
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        try {
            qr.update(sql,
                riceTaskInfo.getTaskName(),
                riceTaskInfo.getTaskDesc(),
                riceTaskInfo.getTaskType(),
                riceTaskInfo.getParameters(),
                riceTaskInfo.getSchedulerServer(),
                riceTaskInfo.getScheduleType(),
                riceTaskInfo.getTimeExpression(),
                riceTaskInfo.getExecuteType(),
                riceTaskInfo.getTaskRetryCount(),
                riceTaskInfo.getNextTriggerTime(),
                riceTaskInfo.getCreateTime(),
                riceTaskInfo.getUpdateTime(),
                riceTaskInfo.getStatus()
            );
        } catch (SQLException e) {
            log.error("addTask exception" + e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public void updateTask(RiceTaskInfo riceTaskInfo) {
        QueryRunner qr = new QueryRunner(dataSource);
        String sql = "update rice_task_info set " +
            "task_name = ?, " +
            "task_desc = ?, " +
            "task_type = ?, " +
            "parameters = ?, " +
            "scheduler_server = ?, " +
            "schedule_type = ?, " +
            "time_expression = ?, " +
            "execute_type = ?, " +
            "task_retry_count = ?, " +
            "next_trigger_time = ?, " +
            "create_time = ?, " +
            "update_time = ?, " +
            "status = ? " +
            "where id = ?";
        try {
            qr.update(sql,
                riceTaskInfo.getTaskName(),
                riceTaskInfo.getTaskDesc(),
                riceTaskInfo.getTaskType(),
                riceTaskInfo.getParameters(),
                riceTaskInfo.getSchedulerServer(),
                riceTaskInfo.getScheduleType(),
                riceTaskInfo.getTimeExpression(),
                riceTaskInfo.getExecuteType(),
                riceTaskInfo.getTaskRetryCount(),
                riceTaskInfo.getNextTriggerTime(),
                riceTaskInfo.getCreateTime(),
                riceTaskInfo.getUpdateTime(),
                riceTaskInfo.getStatus(),
                riceTaskInfo.getId()
            );
        } catch (SQLException e) {
            log.error("updateTask exception" + e);
            throw new RepositoryException(e);
        }

    }

    @Override
    public void taskStatusChange(String taskCode, int status) {
        QueryRunner qr = new QueryRunner(dataSource);
        String sql = "update rice_task_info set status = ? where task_code = ?";
        try {
            qr.update(sql, status, taskCode);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<String> getAllValidTaskCode() {
        QueryRunner qr = new QueryRunner(dataSource);
        final List<String> allTaskCodes = new ArrayList<>();
        String sql = "select task_code from rice_task_info where status != 0";
        try {
            List<Object[]> riceTaskInfos = qr.query(sql, new ArrayListHandler());
            if (riceTaskInfos != null && riceTaskInfos.size() > 0) {
                riceTaskInfos.stream().forEach(taskCode -> {
                    allTaskCodes.add((String) taskCode[0]);
                });
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return allTaskCodes;
    }
}
