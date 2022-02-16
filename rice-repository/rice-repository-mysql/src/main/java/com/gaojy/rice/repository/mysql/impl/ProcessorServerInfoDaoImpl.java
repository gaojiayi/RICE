package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.common.exception.RepositoryException;
import com.gaojy.rice.repository.api.dao.ProcessorServerInfoDao;
import com.gaojy.rice.common.entity.ProcessorServerInfo;
import com.gaojy.rice.repository.mysql.DataSourceFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 * @author gaojy
 * @ClassName ProcessorServerInfoDaoImpl.java
 * @Description TODO
 * @createTime 2022/01/28 10:14:00
 */
public class ProcessorServerInfoDaoImpl implements ProcessorServerInfoDao {
    private final DataSource dataSource = DataSourceFactory.getDataSource();

    @Override
    public List<ProcessorServerInfo> getInfosByServer(String address, int port) throws RepositoryException {
        QueryRunner qr = new QueryRunner(dataSource);
        String sql = "select * from processor_server_info where address = ? and port = ? and status = 1";
        try {
            List<ProcessorServerInfo> processorServerInfoList = qr.query(sql,
                new BeanListHandler<ProcessorServerInfo>(ProcessorServerInfo.class,
                    new BasicRowProcessor(new GenerousBeanProcessor())), address, port);
            return processorServerInfoList;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public int batchCreateOrUpdateInfo(List<ProcessorServerInfo> processorServerInfoList) throws RepositoryException {
        List<ProcessorServerInfo> updateInfos = processorServerInfoList.stream().filter(info -> info.getId() > 0).collect(Collectors.toList());
        List<ProcessorServerInfo> addInfos = processorServerInfoList.stream().filter(info -> info.getId() <= 0).collect(Collectors.toList());
        int count = 0;
        if (updateInfos != null && updateInfos.size() > 0) {
            String sql = "update processor_server_info set address = ?, port = ?, task_code = ?, " +
                "version = ?, latest_active_time = ?, create_time = ?, status = ? where id = ?";
            QueryRunner qr = new QueryRunner(dataSource);
            Object[][] params = new Object[updateInfos.size()][8];

            for (int i = 0; i < updateInfos.size(); i++) {
                ProcessorServerInfo info = updateInfos.get(i);
                params[i][0] = info.getAddress();
                params[i][1] = info.getPort();
                params[i][2] = info.getTaskCode();
                params[i][3] = info.getVersion();
                params[i][4] = info.getLatestActiveTime();
                params[i][5] = info.getCreateTime();
                params[i][6] = info.getStatus();
                params[i][7] = info.getId();
            }
            try {
                int[] batch = qr.batch(sql, params);
                count = count + batch.length;
            } catch (SQLException e) {
                throw new RepositoryException(e);
            }
        }
        if (addInfos != null && addInfos.size() > 0) {
            String sql = "insert into  processor_server_info (address,port,task_code,version,latest_active_time,create_time,status) " +
                " values (?,?,?,?,?,?,?)";
            QueryRunner qr = new QueryRunner(dataSource);
            Object[][] params = new Object[addInfos.size()][7];

            for (int i = 0; i < addInfos.size(); i++) {
                ProcessorServerInfo info = addInfos.get(i);
                params[i][0] = info.getAddress();
                params[i][1] = info.getPort();
                params[i][2] = info.getTaskCode();
                params[i][3] = info.getVersion();
                params[i][4] = info.getLatestActiveTime();
                params[i][5] = info.getCreateTime();
                params[i][6] = info.getStatus();
            }
            try {
                int[] batch = qr.batch(sql, params);
                count = count + batch.length;
            } catch (SQLException e) {
                throw new RepositoryException(e);
            }
        }
        return count;
    }

    @Override public List<ProcessorServerInfo> getInfosByTask(String taskCode) {
        return null;
    }
}
