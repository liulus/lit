package com.lit.support.page;

import com.lit.support.util.lamabda.SerializedFunction;
import com.lit.support.util.lamabda.SerializedLambdaUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * not thread safe
 *
 * @author liulu
 * @version v1.0
 * date 2019-07-26
 */
public class Sort {

    private static final String ORDER_ASC = " ASC";
    private static final String ORDER_DESC = " DESC";

    private static final String DEFAULT_MESSAGE = "sort properties must not null!";

    private Map<String, String> orderByMap = new LinkedHashMap<>();

    public static Sort init() {
        return new Sort();
    }

    public Sort asc(String... properties) {
        Objects.requireNonNull(properties, DEFAULT_MESSAGE);
        for (String property : properties) {
            orderByMap.put(property, ORDER_ASC);
        }
        return this;
    }

    public Sort desc(String... properties) {
        Objects.requireNonNull(properties, DEFAULT_MESSAGE);
        for (String property : properties) {
            orderByMap.put(property, ORDER_DESC);
        }
        return this;
    }

    @SafeVarargs
    public final <T, R> Sort asc(SerializedFunction<T, R>... serializedFunctions) {
        return addSort(ORDER_ASC, serializedFunctions);
    }

    @SafeVarargs
    public final <T, R> Sort desc(SerializedFunction<T, R>... serializedFunctions) {
        return addSort(ORDER_DESC, serializedFunctions);
    }

    @SafeVarargs
    private final <T, R> Sort addSort(String direction, SerializedFunction<T, R>... serializedFunctions) {
        Objects.requireNonNull(serializedFunctions, DEFAULT_MESSAGE);
        for (SerializedFunction<T, R> serializedFunction : serializedFunctions) {
            String property = SerializedLambdaUtils.getProperty(serializedFunction);
            orderByMap.put(property, direction);
        }
        return this;
    }

    public Map<String, String> getOrderByMap() {
        return Collections.unmodifiableMap(orderByMap);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : orderByMap.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append(entry.getValue());
        }
        return sb.toString();
    }
}
