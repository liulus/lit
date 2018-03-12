package com.github.lit.jdbc.statement.select;

import com.github.lit.commons.page.Page;
import com.github.lit.jdbc.enums.JoinType;
import com.github.lit.jdbc.statement.where.Condition;

import java.util.List;

/**
 * User : liulu
 * Date : 2017/6/4 16:48
 * version $Id: Select.java, v 0.1 Exp $
 */
public interface Select<T> extends Condition<Select<T>> {

    /**
     * 查询语句中 要查询的属性(不能和 exclude 同时使用)
     *
     * @param fieldNames 字段名
     * @return Select
     */
    Select<T> include(String... fieldNames);

    /**
     * 查询语句中 要排除的属性
     *
     * @param fieldNames 字段名
     * @return Select
     */
    Select<T> exclude(String... fieldNames);

    /**
     * 多表联合查询要增加的字段
     *
     * @param tableClass 要关联表对应的实体
     * @param fieldNames 实体中的属性
     * @return Select
     */
    Select<T> addField(Class<?> tableClass, String... fieldNames);

    Select<T> function(String funcName);

    Select<T> function(String funcName, String... fieldNames);

    /**
     * Select 语句中添加函数，目前只支持合计函数（Aggregate functions）
     *
     * @param funcName   函数名，如：max，count
     * @param distinct   是否去重
     * @param fieldNames 函数执行的列
     * @return Select
     */
    Select<T> function(String funcName, boolean distinct, String... fieldNames);

    /**
     * 设置字段别名
     *
     * @param alias 别名
     * @return Select
     */
    Select<T> alias(String... alias);

    /**
     * 设置表别名
     *
     * @param alias 别名
     * @return Select
     */
    Select<T> tableAlias(String alias);

    /**
     * 添加join
     *
     * @param tableClass join 表对应的实体
     * @return Select
     */
    JoinExpression<T> join(Class<?> tableClass);

    /**
     * 添加简单join 只是将表名列在 from 后 没有 on 条件
     *
     * @param tableClass join 表对应的实体
     * @return Select
     */
    Select<T> simpleJoin(Class<?> tableClass);

    /**
     * 添加join, 并指定 join 类型
     *
     * @param tableClass join 表对应的实体
     * @param joinType   join类型
     * @return Select
     */
    JoinExpression<T> join(JoinType joinType, Class<?> tableClass);

    /**
     * join 语句的 on 条件, simpleJoin 方法不生效
     *
     * @param table1 on 条件的表1
     * @param field1 on 条件的表1中的属性
     * @param logic  操作符
     * @param table2 on 条件的表2
     * @param field2 on 条件的表2中的属性
     * @return Select
     */
//    Select<T> on(Class<?> table1, String field1, Logic logic, Class<?> table2, String field2);

    SelectExpression<T> and(Class<?> tableClass, String fieldName);

    SelectExpression<T> or(Class<?> tableClass, String fieldName);

    /**
     * join 语句的条件, 只有simpleJoin 生效
     *
     * @param table1 join 条件的表1
     * @param field1 join 条件的表1中的属性
     * @param logic  操作符
     * @param table2 join 条件的表2
     * @param field2 join 条件的表2中的属性
     * @return Select
     */
//    Select<T> joinCondition(Class<?> table1, String field1, Logic logic, Class<?> table2, String field2);

    /**
     * 多表查询时, 可以指定其他表的字段条件
     *
     * @param table  要设置条件的表
     * @param field  字段
     * @param logic  操作符
     * @param values 值
     * @return Select
     */
//    Select<T> and(Class<?> table, String field, Logic logic, Object... values);

    /**
     * 多表查询时, 可以指定其他表的字段条件
     *
     * @param table  要设置条件的表
     * @param field  字段
     * @param logic  操作符
     * @param values 值
     * @return Select
     */
//    Select<T> or(Class<?> table, String field, Logic logic, Object... values);

    /**
     * 添加 group by 的字段
     *
     * @param fields 字段
     * @return Select
     */
    Select<T> groupBy(String... fields);

    SelectExpression<T> having (String fieldName);

    /**
     * 添加 having 条件 默认操作符 =
     *
     * @param fieldName 字段名
     * @param value     值
     * @return Select
     */
//    Select<T> having(String fieldName, Object value);

    /**
     * 添加 having 条件
     *
     * @param fieldName 属性名
     * @param logic     操作符
     * @param values    值
     * @return Select
     */
//    Select<T> having(String fieldName, Logic logic, Object... values);

    /**
     * 添加升序排列属性
     *
     * @param fieldNames 字段名
     * @return Select
     */
    Select<T> asc(String... fieldNames);

    /**
     * 添加降序排列属性
     *
     * @param fieldNames 字段名
     * @return Select
     */
    Select<T> desc(String... fieldNames);

    /**
     * 查询count
     *
     * @return 记录数
     */
    int count();

    /**
     * 查询单条记录
     *
     * @return 实体
     */
    T single();

    /**
     * 查询单条记录
     *
     * @param clazz class
     * @param <E>   return type
     * @return 指定类型
     */
    <E> E single(Class<E> clazz);

    /**
     * 查询列表
     *
     * @return 实体列表
     */
    List<T> list();

    /**
     * 查询列表
     *
     * @param clazz class
     * @param <E>   return type
     * @return 指定类型列表
     */
    <E> List<E> list(Class<E> clazz);

    Select<T> page(Page pager);

    Select<T> page(int pageNum, int pageSize);

    Select<T> page(int pageNum, int pageSize, boolean queryCont);


}
