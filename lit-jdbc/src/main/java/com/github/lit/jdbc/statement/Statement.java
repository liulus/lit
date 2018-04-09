package com.github.lit.jdbc.statement;

import java.io.Serializable;
import java.util.function.Function;

/**
 * User : liulu
 * Date : 2017/6/4 8:50
 * version $Id: Statement.java, v 0.1 Exp $
 */
public interface Statement {

    interface PropertyFunction<T, R> extends Function<T, R>, Serializable {

    }
}
