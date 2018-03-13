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
        appendString("", fieldName);
        return getExpression();
    }

    @Override
    public E and(String fieldName) {
        appendString(" AND ", fieldName);
        return getExpression();
    }

    @Override
    public E or(String fieldName) {
        appendString(" OR ", fieldName);
        return getExpression();
    }

    @Override
    public E bracket(String fieldName) {
        appendString("( ", fieldName);
        return getExpression();
    }

    @Override
    public E primaryKey() {
        appendString("", tableInfo.getPkField());
        return getExpression();
    }

    @Override
    public T and() {
        appendString(" AND ", null);
        return (T) this;
    }

    @Override
    public T or() {
        appendString(" OR ", null);
        return (T) this;
    }


    @Override
    public T end() {
        appendString(" )", null);
        return (T) this;
    }

    protected abstract E getExpression();

    protected void appendString(String operator, String fieldName) {
        where.append(where.length() == 0 ? "" : operator);
        if (fieldName != null && fieldName.length() > 0) {
            where.append(getColumnName(fieldName));
        }
    }

    public void addParamValue(Logic logic, Object... values) {
        addParamValue(where, logic, values);
    }

    protected void addParamValue(StringBuilder sb, Logic logic, Object... values) {
        sb.append(logic.getCode());
        switch (logic) {
            case EQ:
            case NOT_EQ:
            case LT:
            case GT:
            case LTEQ:
            case GTEQ:
            case LIKE:
            case NOT_LIKE:
                sb.append(JDBC_PARAM);
                params.add(values[0]);
                break;
            case IN:
            case NOT_IN:
                sb.append("( ");
                for (Object value : values) {
                    params.add(value);
                    sb.append(JDBC_PARAM).append(", ");
                }
                sb.deleteCharAt(sb.lastIndexOf(",")).append(")");
        }
    }


}
