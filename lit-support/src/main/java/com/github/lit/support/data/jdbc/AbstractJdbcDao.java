package com.github.lit.support.data.jdbc;

/**
 * @author liulu
 * @version 1.0
 * created_at 2020/5/5
 */
public abstract class AbstractJdbcDao<I, E> implements JdbcDao<I, E> {

    @Override
    public I insert(E entity) {

        return null;
    }
}
