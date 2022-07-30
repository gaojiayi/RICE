package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.common.exception.RepositoryException;
import com.gaojy.rice.repository.api.dao.RiceTaskInfoDao;
import com.gaojy.rice.common.entity.RiceTaskInfo;
import com.gaojy.rice.repository.mysql.DataSourceFactory;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 * @author gaojy
 * @ClassName RiceTaskInfoDaoImpl.java
 * @Description 
 * @createTime 2022/01/28 16:25:00
 */
public class RiceTaskInfoDaoImpl implements RiceTaskInfoDao {
    private final DataSource dataSource = DataSourceFactory.getDataSource();

    @Override
    public List<RiceTaskInfo> getInfoByCodes(List<String> taskCodes) {
        QueryRunner qr = new QueryRunner(dataSource);
        String condition = String.join(",", taskCodes);
        String sql = "select * from rice_task_info where task_code in (" + condition + ") and status != 0";
        try {
            List<RiceTaskInfo> riceTaskInfos = qr.query(sql,
                new BeanListHandler<RiceTaskInfo>(RiceTaskInfo.class,
                    new BasicRowProcessor(new GenerousBeanProcessor())));
            return riceTaskInfos;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override public void addTask(RiceTaskInfo riceTaskInfo) {

    }

    @Override public void updateTask(RiceTaskInfo riceTaskInfo) {

    }

    @Override public void taskStatusChange(int status) {

    }

    @Override
    public List<String> getAllValidTaskCode() {
        return null;
    }
}
