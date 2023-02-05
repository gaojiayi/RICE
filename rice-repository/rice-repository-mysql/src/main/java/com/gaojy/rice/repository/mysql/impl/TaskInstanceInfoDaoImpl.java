package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.common.exception.RepositoryException;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.repository.api.dao.TaskInstanceInfoDao;
import com.gaojy.rice.repository.mysql.DataSourceFactory;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskInstanceInfoDaoImpl implements TaskInstanceInfoDao {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.REPOSITORY_LOGGER_NAME);

    private final DataSource dataSource = DataSourceFactory.getDataSource();

    QueryRunner qr = new QueryRunner(dataSource);

    @Override
    public TaskInstanceInfo getInstance(Long id) {
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

    @Override
    public Integer getCountValidInstance() {
        String sql = "select count(id) from task_instance_info where status != 0";
        try {
            return ((Long) qr.query(sql, new ScalarHandler())).intValue();
        } catch (SQLException e) {
            log.error("get valid  instance count exception," + e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<TaskInstanceInfo> getLatestInstance(Integer limit) {
        String sql = "select * from task_instance_info where" +
            " status != 0 and parent_instance_id = 0 " +
            "order by create_time desc limit ?";
        try {
            return qr.query(sql, new BeanListHandler<TaskInstanceInfo>(TaskInstanceInfo.class,
                new BasicRowProcessor(new GenerousBeanProcessor())), limit);

        } catch (SQLException e) {
            log.error("get latest instance exception," + e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public Integer getNumByStatus(Integer status) {
        String sql = "select count(id) from task_instance_info where status = " + status;

        try {
            return ((Long) qr.query(sql, new ScalarHandler())).intValue();
        } catch (SQLException e) {
            log.error("get   instance num by status exception," + e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public Integer queryInstanceNum(String taskCode) {
        String sql = "select count(id) from task_instance_info where parent_instance_id = null ";
        try {
            if (StringUtil.isNotEmpty(taskCode)) {
                sql = "and task_code = ? ";
                return ((Long) qr.query(sql, new ScalarHandler(), taskCode)).intValue();
            } else {
                return ((Long) qr.query(sql, new ScalarHandler())).intValue();

            }
        } catch (SQLException e) {
            log.error("query  instance num exception," + e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<TaskInstanceInfo> queryInstances(String taskCode, Integer pageIndex, Integer pageSize) {
        String taskCodeFilter = StringUtil.isNotEmpty(taskCode) ? " and task_code = " + taskCode : "";
        String sql = "select t.id,task_code,instance_params,parent_instance_id,actual_trigger_time,retry_times," +
            "task_tracker_address,type,result,finished_time,create_time,status from task_instance_info t inner join " +
            "( select id from task_instance_info  where parent_instance_id = null " + taskCodeFilter + "  limit ?,? ) " +
            "as  d on t.id = d.id order by create_time desc;";
        try {
            return qr.query(sql, new BeanListHandler<TaskInstanceInfo>(TaskInstanceInfo.class,
                new BasicRowProcessor(new GenerousBeanProcessor())), (pageIndex - 1) * pageSize, pageSize);

        } catch (SQLException e) {
            log.error("query instances exception," + e);
            throw new RepositoryException(e);
        }
    }
}
