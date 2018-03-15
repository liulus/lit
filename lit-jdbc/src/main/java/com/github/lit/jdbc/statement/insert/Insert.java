package com.github.lit.jdbc.statement.insert;

import com.github.lit.jdbc.statement.Statement;

/**
 * User : liulu
 * Date : 2017/6/4 16:38
 * version $Id: Insert.java, v 0.1 Exp $
 */
public interface Insert extends Statement {

    /**
     * insert 语句操作的字段和值
     * @param fieldName 字段名
     * @param value 字段值
     * @return Insert
     */
    Insert into(String fieldName, Object value);

    /**
     * insert 语句操作的字段和值
     *
     * @param fieldName 字段名
     * @param value     字段值
     * @param isNative  为 true 将不采用 ? 占位符方式, 将值直接拼到 sql 中
     * @return Insert
     */
    Insert into(String fieldName, Object value, boolean isNative);

    /**
     * @return 执行 insert 后的 id 值
     */
    Object execute();

}
