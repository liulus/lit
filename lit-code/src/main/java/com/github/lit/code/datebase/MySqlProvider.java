package com.github.lit.code.datebase;

import com.github.lit.code.context.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User : liulu
 * Date : 2018/2/10 15:50
 * version $Id: MySqlProvider.java, v 0.1 Exp $
 */
public class MySqlProvider implements DataBaseProvider {
    @Override
    public String getDbName() {
        return "MYSQL";
    }

    @Override
    public Map<String, String> getColumnComment(Connection connection, Table table) throws SQLException {

        String sql = "select column_name, column_comment from information_schema.columns where table_schema = ? and table_name = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, table.getCatalog());
        ps.setString(2, table.getName());
        ResultSet resultSet = ps.executeQuery();

        Map<String, String> result = new ConcurrentHashMap<>();

        while (resultSet.next()) {
            result.put(resultSet.getString("column_name").toLowerCase(), resultSet.getString("column_comment"));
        }

        return result;
    }
}
