package com.github.lit.jdbc.sta;

import com.github.lit.jdbc.enums.Logic;

/**
 * User : liulu
 * Date : 2017/6/4 16:34
 * version $Id: Condition.java, v 0.1 Exp $
 */
interface Condition<T extends Condition<T>> extends Statement {

    T idCondition(Object value);

    /**
     * @param logic 操作符
     * @param values   值
     * @return Statement本身
     */
    T idCondition(Logic logic, Object... values);

    /**
     * 添加 where 条件，默认操作符 =
     *
     * @param fieldName 属性名
     * @param value     值
     * @return Statement本身
     */
    T where(String fieldName, Object value);

    /**
     * 添加 where 条件
     *
     * @param fieldName 属性名
     * @param logic  操作符
     * @param values    值
     * @return Statement本身
     */
    T where(String fieldName, Logic logic, Object... values);

    /**
     * 添加 and 条件，默认操作符 =
     *
     * @param fieldName 属性名
     * @param value     值
     * @return Statement本身
     */
    T and(String fieldName, Object value);

    /**
     * 添加 and 条件，
     *
     * @param fieldName 属性名
     * @param values    值
     * @return Statement本身
     */
    T and(String fieldName, Logic logic, Object... values);

    /**
     * 添加带 括号 的 and 条件， 默认操作符 =
     *
     * @param fieldName 属性名
     * @param value     值
     * @return Statement本身
     */
    T andWithBracket(String fieldName, Object value);

    /**
     * 添加 括号 的 and 条件，
     *
     * @param fieldName 属性名
     * @param values    值
     * @return Statement本身
     */
    T andWithBracket(String fieldName, Logic logic, Object... values);

    /**
     * 添加 or 条件，默认操作符 =
     *
     * @param fieldName 属性名
     * @param value     值
     * @return Statement本身
     */
    T or(String fieldName, Object value);

    /**
     * 添加 or 条件，
     *
     * @param fieldName 属性名
     * @param values    值
     * @return Statement本身
     */
    T or(String fieldName, Logic logic, Object... values);

    /**
     * 添加带 括号 的 or 条件， 默认操作符 =
     *
     * @param fieldName 属性名
     * @param value     值
     * @return Statement本身
     */
    T orWithBracket(String fieldName, Object value);

    /**
     * 添加 括号 的 or 条件，
     *
     * @param fieldName 属性名
     * @param values    值
     * @return Statement本身
     */
    T orWithBracket(String fieldName, Logic logic, Object... values);

    /**
     * 添加 where 条件中的结束 括号
     *
     * @return Statement本身
     */
    T end();

    /**
     * 将bean 中不为空的属性作为查询条件
     *
     * @param bean 查询对象
     * @return Statement本身
     */
    T beanCondition(Object bean);


}
