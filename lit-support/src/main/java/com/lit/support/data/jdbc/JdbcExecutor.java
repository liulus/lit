package com.lit.support.data.jdbc;

import com.lit.support.data.SQL;
import com.lit.support.page.Page;
import com.lit.support.page.Pageable;
import com.lit.support.page.Sort;
import com.lit.support.util.lamabda.SerializedFunction;

import java.util.Collection;
import java.util.List;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-09 23:03
 */
public interface JdbcExecutor {

    <E> int insert(E entity);

    <E> int batchInsert(Collection<E> eList);

    <E> int update(E entity);

    <E> int updateSelective(E entity);

    <E> int delete(E entity);

    <E> int deleteById(Class<E> eClass, Long id);

    <E> int deleteByIds(Class<E> eClass, Collection<Long> ids);

    <E> E selectById(Class<E> eClass, Long id);

    <E> List<E> selectByIds(Class<E> eClass, Collection<Long> ids);

    <E> List<E> selectAll(Class<E> eClass);

    <E, R> E selectByProperty(SerializedFunction<E, R> serializedFunction, Object value);

    <E, R> List<E> selectListByProperty(SerializedFunction<E, R> serializedFunction, Object value);

    <E, C> List<E> selectList(Class<E> eClass, C condition);

    <E, C> List<E> selectListWithOrder(Class<E> eClass, C condition, Sort sort);

    <E, C extends Pageable> Page<E> selectPageList(Class<E> eClass, C condition);

    <E> E selectForObject(SQL sql, Object args, Class<E> requiredType);

    <E> List<E> selectForList(SQL sql, Object args, Class<E> requiredType);

    <E> Page<E> selectForPageList(SQL sql, Pageable args, Class<E> requiredType);

    <E> int count(Class<E> eClass);

    <E, R> int countByProperty(SerializedFunction<E, R> serializedFunction, Object value);


}
