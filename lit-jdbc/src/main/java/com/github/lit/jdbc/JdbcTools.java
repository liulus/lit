package com.github.lit.jdbc;

import com.github.lit.commons.page.Page;
import com.github.lit.commons.page.PageList;
import com.github.lit.jdbc.sta.Delete;
import com.github.lit.jdbc.sta.Insert;
import com.github.lit.jdbc.sta.Select;
import com.github.lit.jdbc.sta.Update;

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
     * @param <T>
     * @return
     */
    <T> Object insert(T t);

    /**
     * 根据实体的 id 删除一条记录
     *
     * @param t
     * @param <T>
     * @return
     */
    <T> int delete(T t);

    /**
     * 根据 id 删除对应的记录
     *
     * @param clazz
     * @param ids
     * @param <T>
     * @return
     */
    <T> int deleteByIds(Class<T> clazz, Serializable... ids);

    /**
     * 根据实体 Id 更新一条纪律
     *
     * @param t
     * @param <T>
     * @return
     */
    <T> int update(T t);

    /**
     * 根据实体 Id 更新一条记录
     *
     * @param t            实体
     * @param isIgnoreNull 是否忽略 null 值
     * @param <T>
     * @return
     */
    <T> int update(T t, boolean isIgnoreNull);

    /**
     * 根据 Id 查询一条记录
     *
     * @param clazz
     * @param id
     * @param <T>
     * @return
     */
    <T> T get(Class<T> clazz, Serializable id);

    /**
     * 根据某一属性查询一条记录
     *
     * @param clazz
     * @param propertyName  属性名
     * @param propertyValue 属性值
     * @param <T>
     * @return
     */
    <T> T findByProperty(Class<T> clazz, String propertyName, Object propertyValue);

    /**
     * 根据查询对象查询一条记录
     *
     * @param clazz
     * @param qo
     * @param <T>
     * @param <Qo>
     * @return
     */
    <T, Qo> T queryForSingle(Class<T> clazz, Qo qo);

    /**
     * 根据查询对象查询列表
     *
     * @param clazz
     * @param qo
     * @param <T>
     * @param <Qo>
     * @return
     */
    <T, Qo> List<T> query(Class<T> clazz, Qo qo);

    /**
     * 根据查询对象分页查询
     *
     * @param clazz
     * @param qo
     * @param <T>
     * @param <Qo>
     * @return
     */
    <T, Qo extends Page> PageList<T> queryPageList(Class<T> clazz, Qo qo);

    /**
     * 根据查询对象查询总数
     *
     * @param clazz
     * @param qo
     * @param <T>
     * @param <Qo>
     * @return
     */
    <T, Qo> int count(Class<T> clazz, Qo qo);


    Insert createInsert(Class<?> clazz);

    Delete createDelete(Class<?> clazz);

    Update createUpdate(Class<?> clazz);

    <T> Select<T> createSelect(Class<T> clazz);
}
