package com.github.lit.jdbc.statement.where;

import com.github.lit.jdbc.statement.Expression;
import com.github.lit.jdbc.statement.Statement;

/**
 * User : liulu
 * Date : 2017/6/4 16:34
 * version $Id: Condition.java, v 0.1 Exp $
 */
public interface Condition<T extends Condition, E extends Expression> extends Statement {


    E where(String property);

    <S, R> E where(PropertyFunction<S, R> propertyFunction);

    E and(String property);

    <S, R> E and(PropertyFunction<S, R> propertyFunction);

    E or(String property);

    <S, R> E or(PropertyFunction<S, R> propertyFunction);

    E bracket(String property);

    <S, R> E bracket(PropertyFunction<S, R> propertyFunction);

    E primaryKey();

    T natively();

    T and();

    T or();

    T end();

}
