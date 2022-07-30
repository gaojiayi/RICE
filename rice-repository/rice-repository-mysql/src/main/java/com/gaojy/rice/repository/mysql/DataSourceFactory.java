package com.gaojy.rice.repository.mysql;

import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.exception.RepositoryConnectionException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName DataSourceFactory.java
 * @Description 
 * @createTime 2022/01/17 18:57:00
 */
public class DataSourceFactory {
    public static final Logger logger = LoggerFactory.getLogger(LoggerName.REPOSITORY_LOGGER_NAME);
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private static final String JDBC_URL_PROP = "mysql.jdbc.url";
    private static final String MYSQL_USER_PROP = "mysql.user";
    private static final String MYSQL_PASSWORD_PROP = "mysql.password";

    private static volatile ComboPooledDataSource dataSource;

    private DataSourceFactory() {
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null) {
                    try {
                        dataSource = new ComboPooledDataSource();
                        dataSource.setDriverClass(MYSQL_DRIVER);
                        dataSource.setJdbcUrl(System.getProperty(JDBC_URL_PROP, "jdbc:mysql://localhost:3306/rice"));
                        dataSource.setUser(System.getProperty(MYSQL_USER_PROP, "root"));
                        dataSource.setPassword(System.getProperty(MYSQL_PASSWORD_PROP, "root"));
                        dataSource.setInitialPoolSize(3);
                        dataSource.setMaxPoolSize(10);
                        dataSource.setMinPoolSize(3);
                        dataSource.setAcquireIncrement(3);
                    } catch (PropertyVetoException e) {
                        logger.error("create mysql  datasource error!");
                        throw new RepositoryConnectionException("create mysql  datasource exception!", e);
                    }
                }
            }
        }
        return dataSource;
    }

    /**
     * 获取连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RepositoryConnectionException("connect mysql exception!", e);

        }
        return conn;
    }

    public static void closeDataSoure() {
        dataSource.close();
    }

}
