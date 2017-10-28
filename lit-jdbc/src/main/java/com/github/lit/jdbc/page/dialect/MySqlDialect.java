package com.github.lit.jdbc.page.dialect;

/**
 * User : liulu
 * Date : 2017-3-4 21:18
 * version $Id: MySqlDialect.java, v 0.1 Exp $
 */
public class MySqlDialect extends Dialect {

    private static final MySqlDialect MYSQL_DIALECT = new MySqlDialect();

    private MySqlDialect() {
    }

    public static MySqlDialect getInstance() {
        return MYSQL_DIALECT;
    }

    // MySQL 分页，参数1 ：第几条开始( offset ); 参数2：查询多少条(pageSize)
    private static final String MYSQL_PAGE_SQL = "%s limit %d, %d ";

    @Override
    public String getPageSql(String sql, int pageSize, int pageNum) {
        return String.format(MYSQL_PAGE_SQL, sql, pageSize * (pageNum - 1), pageSize);
    }
}
