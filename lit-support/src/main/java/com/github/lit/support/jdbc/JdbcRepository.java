package com.github.lit.support.jdbc;

import com.github.lit.page.PageParam;
import com.github.lit.util.SerializedFunction;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-09 23:03
 */
@Repository
public interface JdbcRepository {

    <E> int insert(E entity);

    <E> int update(E entity);

    <E> int updateSelective(E entity);

    <E> int delete(E entity);

    <E> int deleteById(Class<E> eClass, Long id);

    <E> E selectById(Class<E> eClass, Long id);

    <E> List<E> selectAll(Class<E> eClass);

    <E, R> E selectByProperty(SerializedFunction<E, R> serializedFunction, Object value);

    <E, R> List<E> selectListByProperty(SerializedFunction<E, R> serializedFunction, Object value);

    <E> E selectForObject(SQL sql, Object args, Class<E> requiredType);

    <E, C> List<E> selectList(Class<E> eClass, C condition);

    <E, C> List<E> selectListWithOrder(Class<E> eClass, C condition, OrderBy orderBy);

    <E> List<E> selectForList(SQL sql, Object args, Class<E> requiredType);

    <E, C extends PageParam> List<E> selectPageList(Class<E> eClass, C condition);

    <E, C extends PageParam> List<E> selectPageListWithOrder(Class<E> eClass, C condition, OrderBy orderBy);

    <E> List<E> selectForPageList(SQL sql, PageParam args, Class<E> requiredType);


}
