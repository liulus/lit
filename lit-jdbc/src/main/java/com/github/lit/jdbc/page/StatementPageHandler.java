package com.github.lit.jdbc.page;

import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * User : liulu
 * Date : 2017/6/4 10:55
 * version $Id: StatementPageHandler.java, v 0.1 Exp $
 */
public interface StatementPageHandler {

    /**
     * 获取分页后的sql
     *
     * @param dbName   数据库名
     * @param sql      查询 sta
     * @param pageSize 每页记录数
     * @param pageNum  当前页
     * @return
     */
    String getPageSql(String dbName, String sql, int pageSize, int pageNum);

    /**
     * 获取查询总数的sql
     *
     * @param dbName 数据库名
     * @param select    查询 sta
     * @return
     */
    String getCountSql(String dbName, PlainSelect select);

}
