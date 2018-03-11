package com.github.lit.jdbc.statement.where;

import com.github.lit.jdbc.enums.Logic;

/**
 * User : liulu
 * Date : 2018-03-11 18:57
 * version $Id: ExpressionImpl.java, v 0.1 Exp $
 */
public class ExpressionImpl<T extends Condition<T>> implements Expression<T> {


    protected T condition;


    ExpressionImpl(T condition) {
        this.condition = condition;
    }

    @Override
    public T equalsTo(Object value) {
        addParamValue(Logic.EQ, value);
        return condition;
    }

    @Override
    public T notEqualsTo(Object value) {
        addParamValue(Logic.NOT_EQ, value);
        return condition;
    }

    @Override
    public T lessThan(Object value) {
        addParamValue(Logic.LT, value);
        return condition;
    }

    @Override
    public T lessThanOrEqual(Object value) {
        addParamValue(Logic.LTEQ, value);
        return condition;
    }

    @Override
    public T graterThan(Object value) {
        addParamValue(Logic.GT, value);
        return condition;
    }

    @Override
    public T graterThanOrEqual(Object value) {
        addParamValue(Logic.GTEQ, value);
        return condition;
    }

    @Override
    public T like(Object value) {
        addParamValue(Logic.LIKE, value);
        return condition;
    }

    @Override
    public T notLike(Object value) {
        addParamValue(Logic.NOT_LIKE, value);
        return condition;
    }

    @Override
    public T isNull() {
        addParamValue(Logic.NULL, (Object[]) null);
        return condition;
    }

    @Override
    public T isNotNull() {
        addParamValue(Logic.NOT_NULL, (Object[]) null);
        return condition;
    }

    @Override
    public T in(Object... values) {
        if (values != null && values.length > 0) {
            addParamValue(Logic.IN, values);
        }
        return condition;
    }

    @Override
    public T notIn(Object... values) {
        if (values != null && values.length > 0) {
            addParamValue(Logic.NOT_IN, values);
        }
        return condition;
    }


    private void addParamValue(Logic logic, Object...values) {
        ((AbstractCondition) condition).addParamValue(logic, values);
    }

}
