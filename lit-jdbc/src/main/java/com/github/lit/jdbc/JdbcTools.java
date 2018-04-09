package com.github.lit.jdbc;

import com.github.lit.jdbc.statement.delete.Delete;
import com.github.lit.jdbc.statement.insert.Insert;
import com.github.lit.jdbc.statement.select.Select;
import com.github.lit.jdbc.statement.update.Update;

import java.io.Serializable;
import java.util.List;

/**
 * User : liulu
 * Date : 2017/6/4 11:13
 * version $Id: JdbcTools.java, v 0.1 Exp $
 */
public interface JdbcTools {

    /**
     * 插入一条记录, 返回这条记录的id
     *
     * @param t   实体对象
     * @param <T> 实体对象类型
     * @return 主键, 自增和序列返回 Long 类型
     */
    <T, ID> ID insert(T t);

    /**
     * 根据实体的 id 删除一条记录
     *
     * @param t   实体对象
     * @param <T> 实体对象类型
     * @return 受影响记录数
     */
    <T> int delete(T t);

    /**
     * 根据 id 删除对应的记录
     *
     * @param clazz 实体对象class
     * @param ids   主键数组
     * @param <T>   实体对象类型
     * @return 受影响记录数
     */
    <T> int deleteByIds(Class<T> clazz, Serializable... ids);

    /**
     * 根据实体 Id 更新一条纪律
     *
     * @param t   实体对象
     * @param <T> 实体对象类型
     * @return 受影响记录数
     */
    <T> int update(T t);

    /**
     * 根据实体 Id 更新一条记录
     *
     * @param t            实体
     * @param isIgnoreNull 是否忽略 null 值
     * @param <T>          实体对象类型
     * @return 受影响记录数
     */
    <T> int update(T t, boolean isIgnoreNull);

    /**
     * 根据 Id 查询一条记录
     *
     * @param clazz 实体对象class
     * @param id    主键
     * @param <T>   实体对象类型
     * @return 实体对象
     */
    <T> T get(Class<T> clazz, Serializable id);

    /**
     * 根据某一属性查询一条记录
     *
     * @param clazz         实体对象class
     * @param property  属性名
     * @param propertyValue 属性值
     * @param <T>           实体对象类型
     * @return 实体对象
     */
    <T> T findByProperty(Class<T> clazz, String property, Object propertyValue);

    <T> T find(Class<T> clazz, String sql, Object[] args);

    <T> List<T> findForList(Class<T> clazz, String sql, Object[] args);


    Insert createInsert(Class<?> clazz);

    Delete createDelete(Class<?> clazz);

    Update createUpdate(Class<?> clazz);

    <T> Select<T> select(Class<T> clazz);
}
