package com.github.lit.support.data.jdbc;

/**
 * @author liulu
 * @version 1.0
 * created_at 2020/5/5
 */
public interface JdbcDao<I, E> {

    I insert(E entity);
}
