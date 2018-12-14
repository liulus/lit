package com.github.lit.support.jdbc;

/**
 * @author mybaitis
 * @version v1.0
 * @see org.apache.ibatis.jdbc.SQL
 * date 2018-12-10 14:20
 */
public class SQL extends AbstractSQL<SQL> {

    @Override
    public SQL getSelf() {
        return this;
    }

    public static SQL init() {
        return new SQL();
    }

}
