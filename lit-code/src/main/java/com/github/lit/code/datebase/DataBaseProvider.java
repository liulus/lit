package com.github.lit.code.datebase;

import com.github.lit.code.context.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * User : liulu
 * Date : 2018/2/10 15:48
 * version $Id: DataBaseProvider.java, v 0.1 Exp $
 */
public interface DataBaseProvider {

    String getDbName();

    Map<String, String> getColumnComment(Connection connection, Table table) throws SQLException;
}
