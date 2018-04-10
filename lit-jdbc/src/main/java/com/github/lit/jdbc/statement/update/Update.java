package com.github.lit.jdbc.statement.update;

import com.github.lit.jdbc.statement.where.Condition;
import com.github.lit.jdbc.statement.where.WhereExpression;

/**
 * User : liulu
 * Date : 2017/6/4 17:00
 * version $Id: Update.java, v 0.1 Exp $
 */
public interface Update extends Condition<Update, WhereExpression<Update>> {

    /**
     * createUpdate 语句中的 set 字段
     *
     * @param property 字段名
     * @param value    字段值
     * @return update
     */
    Update set(String property, Object value);

    <T, R> Update set(PropertyFunction<T, R> property, Object value);

    /**
     * @return 受影响的记录数
     */
    int execute();
}
