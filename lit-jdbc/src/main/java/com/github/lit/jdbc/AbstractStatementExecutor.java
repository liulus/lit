package com.github.lit.jdbc;

import com.github.lit.jdbc.model.StatementContext;

/**
 * User : liulu
 * Date : 2017/6/4 15:32
 * version $Id: AbstractStatementExecutor.java, v 0.1 Exp $
 */
public abstract class AbstractStatementExecutor implements StatementExecutor {


    @Override
    public Object execute(StatementContext context) {

        switch (context.getStatementType()) {
            case INSERT:
                return insert(context);
            case DELETE:
                return delete(context);
            case UPDATE:
                return update(context);
            case SELECT_SINGLE:
                return selectSingle(context);
            case SELECT_LIST:
                return selectList(context);
        }


        return null;
    }

    public abstract  Object insert (StatementContext context);

    public abstract  int delete (StatementContext context);

    public abstract  int update (StatementContext context);

    public abstract  Object selectSingle (StatementContext context);

    public abstract  Object selectList (StatementContext context);


}
