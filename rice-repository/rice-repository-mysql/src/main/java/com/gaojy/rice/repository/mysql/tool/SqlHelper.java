package com.gaojy.rice.repository.mysql.tool;

import java.sql.*;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author gaojy
 * @ClassName SqlHelper.java
 * @Description TODO
 * @createTime 2022/01/25 01:08:00
 */
public class SqlHelper {
    private String url;
    private String username;
    private String password;

    private Connection connection;

    private static final String DRIVER = "com.mysql.jdbc.Driver";

    public SqlHelper(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<HashMap<String, Object>> Get(String sql) {
        List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        Statement statement = null;
        try {
            statement = getStatement();
            ResultSet set = statement.executeQuery(sql);
            ResultSetMetaData meta = set.getMetaData();
            int columnCount = meta.getColumnCount();
            System.out.println(columnCount);

            while (set.next()) {
                HashMap<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String column = meta.getColumnName(i);
                    row.put(column, set.getObject(column));
                }
                result.add(row);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeStatement(statement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> Get(String sql, String columnName) {
        List<T> result = new ArrayList<T>();
        Statement statement = null;
        try {
            statement = getStatement();
            ResultSet set = statement.executeQuery(sql);
            while (set.next()) {
                result.add((T) set.getObject(columnName));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeStatement(statement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public Statement getStatement() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        Connection con = DriverManager.getConnection(url, username, password);
        Statement statement = con.createStatement();
        return statement;
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null) {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(url, username, password);
        }

        return connection;
    }

    public void closeConnection(Connection conn) throws ClassNotFoundException, SQLException {
        if (connection != null) {
            connection.close();
        }

        if (conn != null) {
            conn.close();
        }

        System.out.println("-----------Connection closed now-----------");
    }

    public void closeStatement(Statement statement) throws SQLException {
        if (statement != null) {
            Connection con = statement.getConnection();
            statement.close();
            if (con != null) {
                con.close();
            }
        }
    }
}
