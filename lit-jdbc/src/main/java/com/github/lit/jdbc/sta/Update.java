package com.github.lit.jdbc.sta;

/**
 * User : liulu
 * Date : 2017/6/4 17:00
 * version $Id: Update.java, v 0.1 Exp $
 */
public interface Update extends Condition<Update> {

    /**
     * createUpdate 语句中的 set 字段
     *
     * @param fieldName 字段名
     * @param value 字段值
     * @return
     */
    Update set(String fieldName, Object value);

    /**
     * set 字段对应 的值
     *
     * @param values
     * @return
     */
//    Update values(Object... values);

    Update initEntity(Object entity, boolean isIgnoreNull);

    int execute();
}
