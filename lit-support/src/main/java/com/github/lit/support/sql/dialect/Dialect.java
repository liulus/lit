package com.github.lit.support.sql.dialect;

import com.github.lit.support.sql.Database;

/**
 * User : liulu
 * Date : 2017-3-4 21:11
 * version $Id: Dialect.java, v 0.1 Exp $
 */
public abstract class Dialect {


    public static Dialect valueOf(Database database) {
        switch (database) {
            case DB2:
                return DB2Dialect.getInstance();
            case MYSQL:
                return MySqlDialect.getInstance();
            case ORACLE:
                return OracleDialect.getInstance();
            default:
                return null;
        }
    }


    public abstract String getPageSql(String sql, int pageSize, int pageNum);


}
