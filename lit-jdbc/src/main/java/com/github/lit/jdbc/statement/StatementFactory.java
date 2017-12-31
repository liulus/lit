package com.github.lit.jdbc.statement;

import com.github.lit.jdbc.StatementExecutor;
import com.github.lit.jdbc.page.StatementPageHandler;

/**
 * User : liulu
 * Date : 2017/6/4 10:45
 * version $Id: StatementFactory.java, v 0.1 Exp $
 */
public class StatementFactory {

    public static <T> Select<T> createSelect(Class<T> clazz, StatementExecutor executor, StatementPageHandler pageHandler, String dbName) {
        SelectImpl<T> select = new SelectImpl<>(clazz);
        initStatement(select, executor, pageHandler, dbName);
        return select;
    }

    public static Insert createInsert(Class<?> clazz, StatementExecutor executor, StatementPageHandler pageHandler, String dbName) {
        InsertImpl insert = new InsertImpl(clazz);
        initStatement(insert, executor, pageHandler, dbName);
        return insert;
    }

    public static Delete createDelete(Class<?> clazz, StatementExecutor executor, StatementPageHandler pageHandler, String dbName) {
        DeleteImpl delete = new DeleteImpl(clazz);
        initStatement(delete, executor, pageHandler, dbName);
        return delete;
    }

    public static Update createUpdate(Class<?> clazz, StatementExecutor executor, StatementPageHandler pageHandler, String dbName) {
        UpdateImpl update = new UpdateImpl(clazz);
        initStatement(update, executor, pageHandler, dbName);
        return update;
    }

    private static void initStatement(AbstractStatement statement, StatementExecutor executor, StatementPageHandler pageHandler, String dbName) {
        statement.setDbName(dbName);
        statement.setExecutor(executor);
        statement.setPageHandler(pageHandler);
    }


}
