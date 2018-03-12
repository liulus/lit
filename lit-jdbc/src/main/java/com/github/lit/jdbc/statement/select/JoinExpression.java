package com.github.lit.jdbc.statement.select;

import com.github.lit.jdbc.statement.Expression;

/**
 * User : liulu
 * Date : 2018/3/12 09:20
 * version $Id: JoinExpression.java, v 0.1 Exp $
 */
public class JoinExpression<T> implements Expression {

    private SelectImpl<T> select;

    protected JoinExpression (SelectImpl<T> select) {
        this.select = select;
    }

    public SelectExpression<T> on(Class<?> tableClass, String fieldName) {

        select.on(tableClass, fieldName);

        return select.getSelectExpression();
    }



}
