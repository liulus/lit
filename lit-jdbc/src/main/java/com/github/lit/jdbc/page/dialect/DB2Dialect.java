package com.github.lit.jdbc.page.dialect;

/**
 * User : liulu
 * Date : 2017-3-4 22:23
 * version $Id: DB2Dialect.java, v 0.1 Exp $
 */
public class DB2Dialect extends Dialect {

    private static final DB2Dialect DB2_DIALECT = new DB2Dialect();

    public static DB2Dialect getInstance() {
        return DB2_DIALECT;
    }

    private DB2Dialect() {
    }

    // DB2 分页，参数1 ：第几条开始( offset ); 参数2：第几条为止(maxResult)
    private static final String DB2_PAGE_SQL = "select * from ( select t.*, rownumber() over() rowid from ( %s ) t ) where rowid > %d ) and rowid <= %d ";

    @Override
    public String getPageSql(String sql, int pageSize, int pageNum) {
        return String.format(DB2_PAGE_SQL, sql, pageSize * (pageNum - 1), pageSize * pageNum);
    }
}
