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
public abstract class AbstractCondition<T extends Condition, E extends Expression> extends AbstractStatement implements Condition<T> {

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
//    protected List<Expression> groupBy;

    /**
     * having 语句
     */
//    protected StringBuilder having;

//    @Setter
//    protected E expression;

    protected abstract E getExpression();

    protected AbstractCondition(Class<?> clazz) {
        super(clazz);
        where = new StringBuilder();
//        this.expression = expression;
//        expression = new WhereExpression(this);
    }

    @Override
    public E where(String fieldName) {

        where.append(fieldName);

        return getExpression();
    }

    @Override
    public E and(String fieldName) {
        where.append(fieldName);
        return getExpression();
    }

    @Override
    public E or(String fieldName) {
        where.append(fieldName);
        return getExpression();
    }

    @Override
    public E and() {
        where.append("and");
        return getExpression();
    }

    @Override
    public E or() {
        where.append("or");
        return getExpression();
    }

    @Override
    public E bracket() {
        where.append("(");
        return getExpression();
    }

    @Override
    public E end() {
        where.append(")");
        return getExpression();
    }

//    @Override
//    public T condition(String fieldName, Object value) {
//        return condition(fieldName, Logic.EQ, value);
//    }

//    @Override
//    public T condition(String fileName, Logic logic, Object... values) {
//        String expression = getExpression(getColumnExpression(fileName), logic, values);
//        if (groupBy == null) {
//            where.append(expression);
//        } else {
//            having.append(expression);
//        }
//        return (T) this;
//    }

//    @Override
//    public T idCondition(Object value) {
//        return idCondition(Logic.EQ, value);
//    }

//    @Override
//    public T idCondition(Logic logic, Object... values) {
//        return and(tableInfo.getPkField(), logic, values);
//    }

//    @Override
//    public T where(String fieldName, Object value) {
//        where(fieldName, Logic.EQ, value);
//        return (T) this;
//    }

//    @Override
//    public T where(String fieldName, Logic logic, Object... values) {
//        return condition(fieldName, logic, values);
//    }

//    @Override
//    public T and() {
//        appendOperator(AND);
//        return (T) this;
//    }

//    @Override
//    public T and(String fieldName, Object value) {
//        return this.and(fieldName, Logic.EQ, value);
//    }

//    @Override
//    public T and(String fieldName, Logic logic, Object... values) {
//        return this.and().condition(fieldName, logic, values);
//    }

//    @Override
//    public T or() {
//        appendOperator(OR);
//        return (T) this;
//    }

//    @Override
//    public T or(String fieldName, Object value) {
//        return this.or().condition(fieldName, Logic.EQ, value);
//    }
//
//    @Override
//    public T or(String fieldName, Logic logic, Object... values) {
//        return this.or().condition(fieldName, logic, values);
//    }


//    @Override
//    public T end() {
//        appendOperator(RIGHT_PARENTHESIS);
//        return (T) this;
//    }

    //    @Override
    public T beanCondition(Object bean) {
//        for (String field : tableInfo.getFieldColumnMap().keySet()) {
//            Object value = BeanUtils.invokeReaderMethod(bean, field);
//            if (value != null && (!(value instanceof String) || !((String) value).isEmpty())) {
//                and(field, value);
//            }
//        }
        return (T) this;
    }

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
