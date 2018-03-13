package com.github.lit.jdbc.statement.where;

import com.github.lit.jdbc.statement.Expression;
import com.github.lit.jdbc.statement.Statement;

/**
 * User : liulu
 * Date : 2017/6/4 16:34
 * version $Id: Condition.java, v 0.1 Exp $
 */
public interface Condition<T extends Condition,E extends Expression> extends Statement {


    E where(String fieldName);

    E and(String fieldName);

    E or(String fieldName);

    E bracket(String fieldName);

    E primaryKey();

    T and();

    T or();

    T end();

}
