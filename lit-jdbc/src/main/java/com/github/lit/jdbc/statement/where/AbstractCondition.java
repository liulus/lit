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
    public E where(String property) {
        appendString("", property);
        return getExpression();
    }

    @Override
    public <S, R> E where(PropertyFunction<S, R> property) {
        return where(getProperty(property));
    }

    @Override
    public E and(String property) {
        appendString(" AND ", property);
        return getExpression();
    }

    @Override
    public <S, R> E and(PropertyFunction<S, R> property) {
        return and(getProperty(property));
    }

    @Override
    public E or(String property) {
        appendString(" OR ", property);
        return getExpression();
    }

    @Override
    public <S, R> E or(PropertyFunction<S, R> property) {
        return or(getProperty(property));
    }

    @Override
    public E bracket(String property) {
        appendString("( ", property);
        return getExpression();
    }

    @Override
    public <S, R> E bracket(PropertyFunction<S, R> property) {
        return bracket(getProperty(property));
    }

    @Override
    public E primaryKey() {
        appendString("", tableInfo.getPkProperty());
        return getExpression();
    }

    @Override
    public T natively() {
        isNative = true;
        return (T) this;
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

    protected void appendString(String operator, String property) {
        where.append(where.length() == 0 ? "" : operator);
        if (property != null && property.length() > 0) {
            where.append(getColumn(property));
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
                sb.append(isNative ? String.valueOf(values[0]) : "?");
                if (isNative) {
                    isNative = false;
                } else {
                    params.add(values[0]);
                }
                break;
            case IN:
            case NOT_IN:
                sb.append("( ");
                for (Object value : values) {
                    sb.append(value == null ? "null" : isNative ? value.toString() : "?").append(", ");
                    if (value != null && !isNative) {
                        params.add(value);
                    }
                }
                if (isNative) {
                    isNative = false;
                }
                sb.deleteCharAt(sb.lastIndexOf(",")).append(")");
        }
    }

}
