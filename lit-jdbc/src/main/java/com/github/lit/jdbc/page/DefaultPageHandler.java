package com.github.lit.jdbc.page;

import com.github.lit.jdbc.page.dialect.Dialect;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * User : liulu
 * Date : 2017/6/4 16:12
 * version $Id: DefaultPageHandler.java, v 0.1 Exp $
 */
public class DefaultPageHandler implements StatementPageHandler {


    private Dialect dialect;

    private Dialect getDialect(String dbName) {
        if (dialect == null) {
            dialect = Dialect.valueOf(dbName);
        }
        return dialect;
    }

    @Override
    public String getPageSql(String dbName, String sql, int pageSize, int pageNum) {
        return getDialect(dbName).getPageSql(sql, pageSize, pageNum);
    }

    @Override
    public String getCountSql(String dbName, PlainSelect select) {
        return select.toString();
    }
}
