package com.github.lit.jdbc;

import com.github.lit.jdbc.enums.Logic;
import com.github.lit.jdbc.page.DefaultPageHandler;
import com.github.lit.jdbc.page.StatementPageHandler;
import com.github.lit.jdbc.statement.*;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * User : liulu
 * Date : 2017/6/4 8:45
 * version $Id: AbstractJdbcTools.java, v 0.1 Exp $
 */
public abstract class AbstractJdbcTools implements JdbcTools {

    @Setter
    protected DataSource dataSource;

    @Setter
    private StatementExecutor statementExecutor;

    @Setter
    private StatementPageHandler statementPageHandler;

    @Setter
    private String dbName;


    @Override
    public <T> Object insert(T t) {
        return createInsert(t.getClass()).initEntity(t).execute();
    }

    @Override
    public <T> int delete(T t) {
        return createDelete(t.getClass()).initEntity(t).execute();
    }

    @Override
    public <T> int deleteByIds(Class<T> clazz, Serializable... ids) {
        return createDelete(clazz).idCondition(Logic.IN, (Object[]) ids).execute();
    }

    @Override
    public <T> int update(T t) {
        return createUpdate(t.getClass()).initEntity(t, true).execute();
    }

    @Override
    public <T> int update(T t, boolean isIgnoreNull) {
        return createUpdate(t.getClass()).initEntity(t,isIgnoreNull).execute();
    }

    @Override
    public <T> T get(Class<T> clazz, Serializable id) {
        return createSelect(clazz).idCondition(id).single();
    }

    @Override
    public <T> T findByProperty(Class<T> clazz, String propertyName, Object propertyValue) {
        return createSelect(clazz).where(propertyName, propertyValue).single();
    }

    @Override
    public <T, Qo> int count(Class<T> clazz, Qo qo) {
        return createSelect(clazz).beanCondition(qo).count();
    }

    @Override
    public <T> Select<T> createSelect(Class<T> clazz) {

        return StatementFactory.createSelect(clazz, getStatementExecutor(), getStatementPageHandler(), getDbName());
    }

    @Override
    public Insert createInsert(Class<?> clazz) {

        return StatementFactory.createInsert(clazz, getStatementExecutor(), getStatementPageHandler(), getDbName());
    }

    @Override
    public Delete createDelete(Class<?> clazz) {

        return StatementFactory.createDelete(clazz, getStatementExecutor(), getStatementPageHandler(), getDbName());
    }

    @Override
    public Update createUpdate(Class<?> clazz) {

        return StatementFactory.createUpdate(clazz, getStatementExecutor(), getStatementPageHandler(), getDbName());
    }


    protected abstract StatementExecutor getDefaultExecutor();

    protected abstract String getDefaultDbName();


    private StatementExecutor getStatementExecutor() {
        if (statementExecutor == null) {
            statementExecutor = getDefaultExecutor();
        }
        return statementExecutor;
    }

    private StatementPageHandler getStatementPageHandler() {
        if (statementPageHandler == null) {
            statementPageHandler = new DefaultPageHandler();
        }
        return statementPageHandler;
    }

    private String getDbName() {
        if (dbName == null) {
            dbName = getDefaultDbName();
        }
        return dbName;
    }


}
