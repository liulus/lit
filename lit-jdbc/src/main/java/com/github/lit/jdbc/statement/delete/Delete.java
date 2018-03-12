package com.github.lit.jdbc.statement.delete;

import com.github.lit.jdbc.statement.where.Condition;
import com.github.lit.jdbc.statement.where.WhereExpression;

/**
 * User : liulu
 * Date : 2017/6/4 16:59
 * version $Id: Delete.java, v 0.1 Exp $
 */
public interface Delete extends Condition<Delete, WhereExpression> {

    /**
     * 将实体的 id 作为 delete 的条件
     *
     * @param entity 实体
     * @return Delete
     */
    Delete initEntity(Object entity);

    /**
     * @return 受影响的记录数
     */
    int execute();
}
