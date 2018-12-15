package com.github.lit.support.jdbc;

import com.github.lit.support.util.SerializedFunction;
import com.github.lit.support.util.SerializedLambdaUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-14 17:19
 */
public class OrderBy {

    private static final String ORDER_ASC = " ASC";
    private static final String ORDER_DESC = " DESC";

    private Map<String, String> orderByMap = new LinkedHashMap<>();

    public static OrderBy init() {
        return new OrderBy();
    }

    public <T, R> OrderBy asc(SerializedFunction<T, R> serializedFunction) {
        String property = SerializedLambdaUtils.getProperty(serializedFunction);
        orderByMap.put(property, ORDER_ASC);
        return this;
    }

    public <T, R> OrderBy desc(SerializedFunction<T, R> serializedFunction) {
        String property = SerializedLambdaUtils.getProperty(serializedFunction);
        orderByMap.put(property, ORDER_DESC);
        return this;
    }

    Map<String, String> getOrderByMap() {
        return Collections.unmodifiableMap(orderByMap);
    }




}
