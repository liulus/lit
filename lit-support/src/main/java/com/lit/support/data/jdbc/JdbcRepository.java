package com.lit.support.data.jdbc;

import com.lit.support.data.SQL;
import com.lit.support.data.domain.Page;
import com.lit.support.data.domain.Pageable;
import com.lit.support.data.domain.Sort;
import com.lit.support.util.lamabda.SerializedFunction;

import java.util.Collection;
import java.util.List;

/**
 * @author liulu
 * @version 1.0
 * created_at 2020/5/5
 */
public interface JdbcRepository<E> {

    int insert(E entity);

    int batchInsert(Collection<E> eList);

    int update(E entity);

    int updateSelective(E entity);

    int deleteById(Long id);

    int deleteByIds(Collection<Long> ids);

    E selectById(Long id);

    List<E> selectByIds(Collection<Long> ids);

    List<E> selectAll();

    <R> E selectByProperty(SerializedFunction<E, R> serializedFunction, Object value);

    <R> List<E> selectListByProperty(SerializedFunction<E, R> serializedFunction, Object value);

    <C> List<E> selectList(C condition);

    <C> List<E> selectListWithOrder(C condition, Sort sort);

    <C extends Pageable> Page<E> selectPageList(C condition);

    int countAll();

    <R> int countByProperty(SerializedFunction<E, R> serializedFunction, Object value);

    <T> T selectForObject(SQL sql, Object args, Class<T> requiredType);

    <T> List<T> selectForList(SQL sql, Object args, Class<T> requiredType);

    <T> Page<T> selectForPageList(SQL sql, Pageable args, Class<T> requiredType);
}
