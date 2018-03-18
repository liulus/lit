package com.github.lit.jdbc.statement.where;

import com.github.lit.jdbc.enums.Logic;
import com.github.lit.jdbc.statement.Expression;

/**
 * User : liulu
 * Date : 2018-03-11 18:57
 * version $Id: ExpressionImpl.java, v 0.1 Exp $
 */
public class WhereExpression<T extends Condition> implements Expression {

    /**
     * Select or Update or Delete
     */
    protected T condition;


    public WhereExpression(T condition) {
        this.condition = condition;
    }

    public T equalsTo(Object value) {
        if (value == null) {
            isNull();
            return condition;
        }
        addParamValue(Logic.EQ, value);
        return condition;
    }

    public T notEqualsTo(Object value) {
        if (value == null) {
            isNotNull();
            return condition;
        }
        addParamValue(Logic.NOT_EQ, value);
        return condition;
    }

    public T lessThan(Object value) {
        addParamValue(Logic.LT, value);
        return condition;
    }

    public T lessThanOrEqual(Object value) {
        addParamValue(Logic.LTEQ, value);
        return condition;
    }

    public T graterThan(Object value) {
        addParamValue(Logic.GT, value);
        return condition;
    }

    public T graterThanOrEqual(Object value) {
        addParamValue(Logic.GTEQ, value);
        return condition;
    }

    public T like(Object value) {
        addParamValue(Logic.LIKE, value);
        return condition;
    }

    public T notLike(Object value) {
        addParamValue(Logic.NOT_LIKE, value);
        return condition;
    }

    public T isNull() {
        addParamValue(Logic.NULL, (Object[]) null);
        return condition;
    }

    public T isNotNull() {
        addParamValue(Logic.NOT_NULL, (Object[]) null);
        return condition;
    }

    public T in(Object... values) {
        if (values == null || values.length == 0 || values[0] == null) {
            return condition;
        }
        if (values.length == 1) {
            addParamValue(Logic.EQ, values[0]);
            return condition;
        }
        addParamValue(Logic.IN, values);
        return condition;
    }

    public T notIn(Object... values) {
        if (values == null || values.length == 0 || values[0] == null) {
            return condition;
        }
        if (values.length == 1) {
            addParamValue(Logic.NOT_EQ, values[0]);
            return condition;
        }
        addParamValue(Logic.NOT_IN, values);
        return condition;
    }


    private void addParamValue(Logic logic, Object... values) {
        ((AbstractCondition) condition).addParamValue(logic, values);
    }

}
