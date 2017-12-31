package com.github.lit.jdbc.statement;

import com.github.lit.commons.bean.BeanUtils;
import com.github.lit.jdbc.enums.Logic;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.List;

/**
 * User : liulu
 * Date : 2017/6/4 8:51
 * version $Id: AbstractCondition.java, v 0.1 Exp $
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCondition<T extends Condition<T>> extends AbstractStatement implements Condition<T> {

    private static final String AND = "AND";

    private static final String OR = "OR";

    private static final String EMPTY = " ";

    private static final String LEFT_PARENTHESIS = "(";

    private static final String RIGHT_PARENTHESIS = ")";

    /**
     * where 语句
     */
    protected StringBuilder where;

    /**
     * groupBy 语句
     */
    protected List<Expression> groupBy;

    /**
     * having 语句
     */
    protected StringBuilder having;

    AbstractCondition(Class<?> clazz) {
        super(clazz);
        where = new StringBuilder();
    }

    @Override
    public T condition(String fieldName, Object value) {
        return condition(fieldName, Logic.EQ, value);
    }

    @Override
    public T condition(String fileName, Logic logic, Object... values) {
        String expression = getExpression(buildColumn(fileName), logic, values);
        if (groupBy == null) {
            where.append(expression);
        } else {
            having.append(expression);
        }
        return (T) this;
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
        return condition(fieldName, logic, values);
    }

    @Override
    public T and() {
        appendOperator(AND);
        return (T) this;
    }

    @Override
    public T and(String fieldName, Object value) {
        return this.and(fieldName, Logic.EQ, value);
    }

    @Override
    public T and(String fieldName, Logic logic, Object... values) {
        return this.and().condition(fieldName, logic, values);
    }

    @Override
    public T or() {
        appendOperator(OR);
        return (T) this;
    }

    @Override
    public T or(String fieldName, Object value) {
        return this.or().condition(fieldName, Logic.EQ, value);
    }

    @Override
    public T or(String fieldName, Logic logic, Object... values) {
        return this.or().condition(fieldName, logic, values);
    }

    @Override
    public T parenthesis() {
        appendOperator(LEFT_PARENTHESIS);
        return (T) this;
    }

    @Override
    public T end() {
        appendOperator(RIGHT_PARENTHESIS);
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

    private void appendOperator(String logic) {
        if (groupBy == null) {
            if (where.length() != 0) {
                where.append(EMPTY).append(logic).append(EMPTY);
            }
            return;
        }
        if (having.length() != 0) {
            having.append(EMPTY).append(logic).append(EMPTY);
        }
    }

    protected String getExpression(Column column, Logic logic, Object... values) {

        StringBuilder result = new StringBuilder(column.toString());

        if (values == null || values.length == 0 || values[0] == null) {
            if (logic != Logic.NOT_NULL) {
                logic = Logic.NULL;
            }
            return result.append(logic.getValue()).toString();
        }

        result.append(logic.getValue());
        if (isBinaryLogic(logic)) {
            params.add(values[0]);
            return result.append(JDBC_PARAM).toString();
        }

        // 剩下 IN 和 NOT_IN
        result.append(LEFT_PARENTHESIS).append(EMPTY);
        for (int i = 0; i < values.length; i++) {
            params.add(values[i]);
            result.append(JDBC_PARAM);
            if (i != values.length - 1) {
                result.append(", ");
            }
        }
        return result.append(EMPTY).append(RIGHT_PARENTHESIS).toString();
    }

    private boolean isBinaryLogic(Logic logic) {
        switch (logic) {
            case EQ:
            case NOT_EQ:
            case LT:
            case GT:
            case LTEQ:
            case GTEQ:
            case LIKE:
            case NOT_LIKE:
                return true;
            default:
                return false;
        }
    }


}
