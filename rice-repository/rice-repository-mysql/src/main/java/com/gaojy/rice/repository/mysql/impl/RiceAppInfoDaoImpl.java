package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.entity.RiceAppGroupInfo;
import com.gaojy.rice.common.entity.RiceAppInfo;
import com.gaojy.rice.common.exception.RepositoryException;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.repository.api.dao.RiceAppInfoDao;
import com.gaojy.rice.repository.mysql.DataSourceFactory;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName RiceAppInfoDaoImpl.java
 * @Description
 * @createTime 2022/11/20 14:23:00
 */
public class RiceAppInfoDaoImpl implements RiceAppInfoDao {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.REPOSITORY_LOGGER_NAME);

    private final DataSource dataSource = DataSourceFactory.getDataSource();

    QueryRunner qr = new QueryRunner(dataSource);

    @Override
    public List<RiceAppGroupInfo> getCountByName(Integer limit) {
        String sql = "select count(id) as num , app_name from rice_app_info group by app_name limit ? ";
        try {
            return qr.query(sql, new BeanListHandler<RiceAppGroupInfo>(RiceAppGroupInfo.class,
                new BasicRowProcessor(new GenerousBeanProcessor())), limit);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<RiceAppInfo> queryApps(String appName, Integer pageIndex, Integer pageSize) {
        String appNameFilter = StringUtil.isNotEmpty(appName) ? " and app_name like %" + appName + "%" : "";
        String sql = "select t.id,app_name,app_desc,t.create_time,t.status  from rice_app_info t " +
            "INNER  JOIN (select id from rice_app_info where status != 0 " + appNameFilter + " limit ?,?)  as k ON t.id=k.id;";
        try {
            return qr.query(sql, new BeanListHandler<RiceAppInfo>(RiceAppInfo.class,
                new BasicRowProcessor(new GenerousBeanProcessor())), (pageIndex - 1) * pageSize, pageSize);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Integer queryAppsCount(String appName) {
        String appNameFilter = StringUtil.isNotEmpty(appName) ? " and app_name like %" + appName + "%" : "";
        String sql = "select count(id) from rice_app_info where status != 0"+appNameFilter;
        try {
            return ((Long) qr.query(sql, new ScalarHandler())).intValue();
        } catch (SQLException e) {
            log.error("query valid  app count exception," + e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public void createApp(RiceAppInfo appInfo) {
        String sql = "insert into rice_app_info (app_name,app_desc,create_time,status) values (?,?,?,?) ";
        try {
            qr.update(sql,
                appInfo.getAppName(),
                appInfo.getAppDesc(),
                appInfo.getCreateTime(),
                appInfo.getStatus());
        } catch (SQLException e) {
            log.error("append log error,log={}", log.toString());
            throw new RepositoryException(e);
        }

    }
}
