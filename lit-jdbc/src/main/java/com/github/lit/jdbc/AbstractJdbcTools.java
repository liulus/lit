package com.github.lit.jdbc;

import com.github.lit.commons.util.Assert;
import com.github.lit.jdbc.model.StatementContext;
import com.github.lit.jdbc.page.DefaultPageHandler;
import com.github.lit.jdbc.page.StatementPageHandler;
import com.github.lit.jdbc.statement.StatementFactory;
import com.github.lit.jdbc.statement.delete.Delete;
import com.github.lit.jdbc.statement.delete.DeleteImpl;
import com.github.lit.jdbc.statement.insert.Insert;
import com.github.lit.jdbc.statement.insert.InsertImpl;
import com.github.lit.jdbc.statement.select.Select;
import com.github.lit.jdbc.statement.update.Update;
import com.github.lit.jdbc.statement.update.UpdateImpl;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

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
    public <T, ID> ID insert(T t) {
        Assert.notNull(t, "entity must not be null");
        InsertImpl insert = (InsertImpl) createInsert(t.getClass());

        //noinspection unchecked
        return (ID) insert.initEntity(t).execute();
    }

    @Override
    public <T> int delete(T t) {
        Assert.notNull(t, "entity must not be null");
        DeleteImpl delete = (DeleteImpl) createDelete(t.getClass());
        return delete.initEntity(t).execute();
    }

    @Override
    public <T> int deleteByIds(Class<T> clazz, Serializable... ids) {
        return createDelete(clazz).primaryKey().in((Object[]) ids).execute();
    }

    @Override
    public <T> int update(T t) {
        return update(t, true);
    }

    @Override
    public <T> int update(T t, boolean isIgnoreNull) {
        Assert.notNull(t, "entity must not be null");
        UpdateImpl update = (UpdateImpl) createUpdate(t.getClass());
        return update.initEntity(t, isIgnoreNull).execute();
    }

    @Override
    public <T> T get(Class<T> clazz, Serializable id) {
        return select(clazz).primaryKey().equalsTo(id).single();
    }

    @Override
    public <T> T findByProperty(Class<T> clazz, String propertyName, Object propertyValue) {
        return select(clazz).where(propertyName).equalsTo(propertyValue).single();
    }

    @Override
    public <T> T find(Class<T> clazz, String sql, Object[] args) {
        StatementContext context = new StatementContext();
        context.setEntityClass(clazz);
        context.setRequireType(clazz);
        context.setSql(sql);
        context.setParams(Arrays.asList(args));
        context.setStatementType(StatementContext.Type.SELECT_SINGLE);

        //noinspection unchecked
        return (T) statementExecutor.execute(context);
    }

    @Override
    public <T> List<T> findForList(Class<T> clazz, String sql, Object[] args) {
        StatementContext context = new StatementContext();
        context.setEntityClass(clazz);
        context.setRequireType(clazz);
        context.setSql(sql);
        context.setParams(Arrays.asList(args));
        context.setStatementType(StatementContext.Type.SELECT_LIST);

        //noinspection unchecked
        return (List<T>) statementExecutor.execute(context);
    }

    @Override
    public <T> Select<T> select(Class<T> clazz) {

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
