package com.github.lit.support.sql;

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

    public static SQL baseSelect(Class<?> cls) {
        TableMetaDate metaDate = TableMetaDate.forClass(cls);
        return SQL.init().SELECT(metaDate.getAllColumns()).FROM(metaDate.getTableName());
    }

    public static SQL count(Class<?> cls) {
        TableMetaDate metaDate = TableMetaDate.forClass(cls);
        return SQL.init().SELECT("count(*)").FROM(metaDate.getTableName());
    }

}
