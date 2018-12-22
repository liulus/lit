package com.github.lit.support.page;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-15 13:41
 */
@ToString
public class Page<T> {

    private static final Page EMPTY = new Page();

    @Getter
    @Setter
    private List<T> content = Collections.emptyList();

    @Getter
    @Setter
    private PageInfo pageInfo;

    private Map<String, Object> additional;

    @SuppressWarnings("unchecked")
    public static <E> Page<E> emptyPage() {
        return (Page<E>) EMPTY;
    }


    public void add(String key, Object value) {
        if (additional == null) {
            additional = new HashMap<>();
        }
        additional.put(key, value);
    }

    public void addAll(Map<String, Object> map) {
        if (map == null) {
            return;
        }
        if (additional == null) {
            additional = new HashMap<>();
        }
        additional.putAll(map);
    }

    public Map<String, Object> getAdditional() {
        if (additional == null) {
            return null;
        }
        return Collections.unmodifiableMap(additional);
    }


}
