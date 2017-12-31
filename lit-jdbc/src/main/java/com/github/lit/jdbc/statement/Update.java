package com.github.lit.jdbc.statement;

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
     * @param value     字段值
     * @return update
     */
    Update set(String fieldName, Object value);

    /**
     * 初始化实体条件
     *
     * @param entity       实体
     * @param isIgnoreNull 是否忽略null值
     * @return update
     */
    Update initEntity(Object entity, boolean isIgnoreNull);

    int execute();
}
