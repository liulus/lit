package com.github.lit.jdbc.statement.where;

import com.github.lit.jdbc.enums.Logic;
import com.github.lit.jdbc.statement.AbstractStatement;
import com.github.lit.jdbc.statement.Expression;

/**
 * User : liulu
 * Date : 2017/6/4 8:51
 * version $Id: AbstractCondition.java, v 0.1 Exp $
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCondition<T extends Condition, E extends Expression> extends AbstractStatement implements Condition<T, E> {

    protected StringBuilder where;


    protected AbstractCondition(Class<?> clazz) {
        super(clazz);
        where = new StringBuilder();
    }

    @Override
    public E where(String fieldName) {
        where.append(getColumnName(fieldName));
        return getExpression();
    }

    @Override
    public E and(String fieldName) {
        where.append(where.length() == 0 ? "" : " AND ").append(getColumnName(fieldName));
        return getExpression();
    }

    @Override
    public E or(String fieldName) {
        where.append(where.length() == 0 ? "" : " OR ").append(getColumnName(fieldName));
        return getExpression();
    }

    @Override
    public E bracket(String fieldName) {
        where.append("( ").append(getColumnName(fieldName));
        return getExpression();
    }

    @Override
    public T and() {
        where.append(" AND ");
        return (T) this;
    }

    @Override
    public T or() {
        where.append(" OR ");
        return (T) this;
    }


    @Override
    public T end() {
        where.append(" )");
        return (T) this;
    }

    protected abstract E getExpression();

    public void addParamValue(Logic logic, Object... values) {
        where.append(logic.getCode());
        switch (logic) {
            case EQ:
            case NOT_EQ:
            case LT:
            case GT:
            case LTEQ:
            case GTEQ:
            case LIKE:
            case NOT_LIKE:
                where.append(JDBC_PARAM);
                params.add(values[0]);
                break;
            case IN:
            case NOT_IN:
                where.append("( ");
                for (Object value : values) {
                    params.add(value);
                    where.append(JDBC_PARAM).append(", ");
                }
                where.deleteCharAt(where.lastIndexOf(",")).append(")");
        }

    }


}
