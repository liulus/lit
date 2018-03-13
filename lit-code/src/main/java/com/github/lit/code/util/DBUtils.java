package com.github.lit.code.util;

import com.github.lit.code.config.JdbcConfig;
import com.github.lit.code.context.GenerationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * User : liulu
 * Date : 2018/2/9 15:20
 * version $Id: DBUtils.java, v 0.1 Exp $
 */
public class DBUtils {

    private static Connection connection;

    private static String getDriverClass(String dbName) {
        if (dbName == null || dbName.isEmpty()) {
            return dbName;
        }
        switch (dbName.toUpperCase()) {
            case "MYSQL":
                return "com.mysql.jdbc.Driver";
            case "ORACLE":
                return "oracle.jdbc.driver.OracleDriver";
            default:
                return "";
        }
    }


    public synchronized static void createConnection(JdbcConfig jdbcConfig) {

        try {
            if (jdbcConfig.getDriverClass() == null && jdbcConfig.getDbName() != null) {
                jdbcConfig.setDriverClass(getDriverClass(jdbcConfig.getDbName()));
                Class.forName(jdbcConfig.getDriverClass());
            }
            connection = DriverManager.getConnection(jdbcConfig.getUrl(), jdbcConfig.getUser(), jdbcConfig.getPassword());
            if (jdbcConfig.getDbName() == null || jdbcConfig.getDbName().isEmpty()) {
                jdbcConfig.setDbName(connection.getMetaData().getDatabaseProductName().toUpperCase());
            }
        } catch (Exception e) {
            throw new GenerationException("创建数据库连接失败! 检查连接信息是否正确!");
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                throw new GenerationException("数据库连接已失效, 请重新创建!");
            }
            return connection;
        } catch (SQLException e) {
            throw new GenerationException("数据库连接异常!", e);
        }
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            //
        }
    }
}
