package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.repository.api.dao.ProcessorServerInfoDao;
import com.gaojy.rice.common.entity.ProcessorServerInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName TestProcessorServerInfoDao.java
 * @Description 
 * @createTime 2022/01/28 11:07:00
 */
@RunWith(JUnit4.class)
public class TestProcessorServerInfoDao {
    ProcessorServerInfoDao dao = new ProcessorServerInfoDaoImpl();

    @Test
    public void processorServerInfoDaoTest() throws SQLException {
        List<ProcessorServerInfo> infos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ProcessorServerInfo info = new ProcessorServerInfo();
            info.setAddress("127.0.0." + i);
            info.setPort(1234);
            info.setLatestActiveTime(new Date());
            info.setCreateTime(new Date());
            info.setStatus(0);
            info.setVersion("-1");
            info.setTaskCode("test_task_code");
            infos.add(info);
        }
        int i = dao.batchCreateOrUpdateInfo(infos);
        Assert.assertEquals(10, i);
        List<ProcessorServerInfo> server = dao.getInfosByServer("127.0.0.5", 1234);
        server.get(0).setStatus(1);
        i = dao.batchCreateOrUpdateInfo(server);
        Assert.assertEquals(1, i);
    }
}
