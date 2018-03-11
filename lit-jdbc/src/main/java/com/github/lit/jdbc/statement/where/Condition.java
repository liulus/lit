package com.github.lit.jdbc.statement.where;

import com.github.lit.jdbc.statement.Statement;

/**
 * User : liulu
 * Date : 2017/6/4 16:34
 * version $Id: Condition.java, v 0.1 Exp $
 */
public interface Condition<T extends Condition<T>> extends Statement {


    Expression<T> where(String fieldName);

    Expression<T> and(String fieldName);

    Expression<T> or(String fieldName);

    Expression<T> and();

    Expression<T> or();

    Expression<T> bracket();

    Expression<T> end();



//    T condition(String fieldName, Object value);
//
//    T condition(String fileName, Logic logic, Object...values);
//
//    T idCondition(Object value);

    /**
     * @param logic  操作符
     * @param values 值
     * @return Statement本身
     */
//    T idCondition(Logic logic, Object... values);

    /**
     * 添加 where 条件，默认操作符 =
     *
     * @param fieldName 属性名
     * @param value     值
     * @return Statement本身
     */
//    T where(String fieldName, Object value);

    /**
     * 添加 where 条件
     *
     * @param fieldName 属性名
     * @param logic     操作符
     * @param values    值
     * @return Statement本身
     */
//    T where(String fieldName, Logic logic, Object... values);

    /**
     * 添加 and 关键字
     *
     * @return Statement本身
     */
//    T and();

    /**
     * 添加 and 条件，默认操作符 =
     *
     * @param fieldName 属性名
     * @param value     值
     * @return Statement本身
     */
//    T and(String fieldName, Object value);

    /**
     * 添加 and 条件，
     *
     * @param fieldName 属性名
     * @param logic     操作逻辑
     * @param values    值
     * @return Statement本身
     */
//    T and(String fieldName, Logic logic, Object... values);

    /**
     * 添加 or 关键字
     *
     * @return Statement本身
     */
//    T or();

    /**
     * 添加 or 条件，默认操作符 =
     *
     * @param fieldName 属性名
     * @param value     值
     * @return Statement本身
     */
//    T or(String fieldName, Object value);

    /**
     * 添加 or 条件，
     *
     * @param fieldName 属性名
     * @param logic     操作逻辑
     * @param values    值
     * @return Statement本身
     */
//    T or(String fieldName, Logic logic, Object... values);

    /**
     * 添加 (
     *
     * @return Statement本身
     */
//    T parenthesis();

    /**
     * 添加 )
     *
     * @return Statement本身
     */
//    T end();

    /**
     * 将bean 中不为空的属性作为查询条件
     *
     * @param bean 查询对象
     * @return Statement本身
     */
//    T beanCondition(Object bean);


}
