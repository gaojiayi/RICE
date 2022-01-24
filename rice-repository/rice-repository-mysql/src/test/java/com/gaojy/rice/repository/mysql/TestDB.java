package com.gaojy.rice.repository.mysql;

import com.gaojy.rice.repository.mysql.entity.RiceAppInfo;
import java.util.Date;
import java.sql.SQLException;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName TestDB.java
 * @Description TODO
 * @createTime 2022/01/25 01:56:00
 */
@RunWith(JUnit4.class)
public class TestDB {

    @Test
    public void testDB() throws SQLException {

        MysqlRepository repository = new MysqlRepository();
        QueryRunner qr = new QueryRunner(repository.getDataSource());
        RiceAppInfo riceAppInfo = new RiceAppInfo();
        riceAppInfo.setAppName("testApp");
        riceAppInfo.setAppDesc("testApp");
        riceAppInfo.setCreateTime(new Date());
        riceAppInfo.setStatus(1);
        qr.update("insert into rice_app_info(app_name,app_desc,create_time,status) values(?,?,?,?)",
            riceAppInfo.getAppName(),riceAppInfo.getAppDesc(),riceAppInfo.getCreateTime(),riceAppInfo.getStatus());

        RiceAppInfo riceAppInfo1 = qr.query("select * from rice_app_info",
            new BeanHandler<RiceAppInfo>(RiceAppInfo.class,
                new BasicRowProcessor(new GenerousBeanProcessor())));
        Assert.assertEquals(riceAppInfo1.getAppName(),riceAppInfo.getAppName());
    }

}
