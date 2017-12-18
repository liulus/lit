package com.github.lit.jdbc.sta;

import com.github.lit.commons.bean.BeanUtils;
import com.github.lit.jdbc.enums.Logic;
import com.github.lit.jdbc.spi.expr.LeftParenthesis;
import com.github.lit.jdbc.spi.expr.RightParenthesis;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * User : liulu
 * Date : 2017/6/4 8:51
 * version $Id: AbstractCondition.java, v 0.1 Exp $
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCondition<T extends Condition<T>> extends AbstractStatement implements Condition<T> {

    /**
     * where 语句
     */
    protected Expression where;

    /**
     * groupBy 语句
     */
    protected List<Expression> groupBy;

    /**
     * having 语句
     */
    protected Expression having;

    AbstractCondition(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public T idCondition(Object value) {
        return idCondition(Logic.EQ, value);
    }

    @Override
    public T idCondition(Logic logic, Object... values) {
        return and(tableInfo.getPkField(), logic, values);
    }

    @Override
    public T where(String fieldName, Object value) {
        where(fieldName, Logic.EQ, value);
        return (T) this;
    }

    @Override
    public T where(String fieldName, Logic logic, Object... values) {
        addExpr(fieldName, logic, true, values);
        return (T) this;
    }

    @Override
    public T and() {
        if (where != null) {
            where = new AndExpression(where, EMPTY_EXPR);
        }
        return (T) this;
    }

    @Override
    public T and(String fieldName, Object value) {
        this.and(fieldName, Logic.EQ, value);
        return (T) this;
    }

    @Override
    public T and(String fieldName, Logic logic, Object... values) {
        addExpr(fieldName, logic, true, values);
        return (T) this;
    }

    @Override
    public T or() {
        if (where != null) {
            where = new OrExpression(where, EMPTY_EXPR);
        }
        return (T) this;
    }

    @Override
    public T or(String fieldName, Object value) {
        this.or(fieldName, Logic.EQ, value);
        return (T) this;
    }

    @Override
    public T or(String fieldName, Logic logic, Object... values) {
        addExpr(fieldName, logic, false, values);
        return (T) this;
    }

    @Override
    public T parenthesis() {
        if (where != null) {
            where = new LeftParenthesis(where);
        }
        return (T) this;
    }

    @Override
    public T end() {
        if (where != null) {
            where = new RightParenthesis(where);
        }
        return (T) this;
    }

    @Override
    public T beanCondition(Object bean) {
        for (String field : tableInfo.getFieldColumnMap().keySet()) {
            Object value = BeanUtils.invokeReaderMethod(bean, field);
            if (value != null && (!(value instanceof String) || !((String) value).isEmpty())) {
                and(field, value);
            }
        }

        return (T) this;
    }


    private void addExpr(String fieldName, Logic logic, boolean isAnd, Object... values) {
        Expression expression = getExpression(buildColumn(fieldName), logic, values);
        if (expression != null) {
            if (groupBy == null) {
                where = where == null ? expression :
                        isAnd ? new AndExpression(where, expression) :
                                new OrExpression(where, expression);
            } else {
                having = having == null ? expression :
                        isAnd ? new AndExpression(having, expression) :
                                new OrExpression(having, expression);
            }
        }
    }


    protected Expression getExpression(Column column, Logic logic, Object... values) {

        if (values == null || values.length == 0 || values[0] == null) {
            IsNullExpression isNullExpression = new IsNullExpression();
            isNullExpression.setLeftExpression(column);
            if (logic == Logic.NOT_NULL) {
                isNullExpression.setNot(true);
            }
            return isNullExpression;
        }

        BinaryExpression expr = getBinaryExpression(logic);
        if (expr != null) {
            expr.setLeftExpression(column);
            expr.setRightExpression(PARAM_EXPR);
            params.add(values[0]);
            return expr;
        }

        // 剩下 IN 和 NOT_IN
        List<Expression> expressions = new ArrayList<>(values.length);
        for (Object obj : values) {
            expressions.add(PARAM_EXPR);
            params.add(obj);
        }

        InExpression inExpression = new InExpression(column, new ExpressionList(expressions));
        if (logic == Logic.NOT_IN) {
            inExpression.setNot(true);
        }
        return inExpression;
    }

    protected BinaryExpression getBinaryExpression(Logic logic) {
        switch (logic) {
            case EQ:
                return new EqualsTo();
            case NOT_EQ:
                return new NotEqualsTo();
            case LT:
                return new MinorThan();
            case GT:
                return new GreaterThan();
            case LTEQ:
                return new MinorThanEquals();
            case GTEQ:
                return new GreaterThanEquals();
            case LIKE:
                return new LikeExpression();
            case NOT_LIKE:
                LikeExpression likeExpression = new LikeExpression();
                likeExpression.setNot(true);
                return likeExpression;
        }
        return null;
    }


}
