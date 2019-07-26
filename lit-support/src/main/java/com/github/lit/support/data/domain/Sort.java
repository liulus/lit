package com.github.lit.support.data.domain;

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

    private Map<String, String> orderByMap = new LinkedHashMap<>();

    private String[] lastProperties;
    private boolean direction = false;

    public static Sort init() {
        return new Sort();
    }

    public static Sort initBy(String... properties) {
        return init().by(properties);
    }

    public Sort by(String... properties) {
        Objects.requireNonNull(properties, "sort properties must not null!");
        lastProperties = properties;
        direction = true;
        return this;
    }

    public Sort asc() {
        return addOrder(ORDER_ASC);
    }

    public Sort desc() {
        return addOrder(ORDER_DESC);
    }

    private Sort addOrder(String orderDesc) {
        if (direction) {
            for (String property : lastProperties) {
                orderByMap.put(property, orderDesc);
            }
        }
        direction = false;
        return this;
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
