package com.github.lit.jdbc.page.dialect;

/**
 * User : liulu
 * Date : 2017-3-4 22:26
 * version $Id: OracleDialect.java, v 0.1 Exp $
 */
public class OracleDialect extends Dialect {

    private static final OracleDialect ORACLE_DIALECT = new OracleDialect();

    public static OracleDialect getInstance() {
        return ORACLE_DIALECT;
    }

    private OracleDialect() {
    }

    // Oracle 分页，参数1：第几条为止(maxResult); 参数2 ：第几条开始( offset )
    private static final String ORACLE_PAGE_SQL = "select * from (select t.*, rownum rowno from ( %s ) t where rownum <= %d ) where rowno > %d ";


    @Override
    public String getPageSql(String sql, int pageSize, int pageNum) {
        return String.format(ORACLE_PAGE_SQL, sql, pageSize * pageNum, pageSize * (pageNum - 1));
    }
}
