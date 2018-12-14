package com.github.lit.util;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Serialize
 * @author liulu
 * @version v1.0
 * date 2018-12-11 19:42
 */
public interface SerializedFunction<T, R> extends Function<T, R>, Serializable {
}
