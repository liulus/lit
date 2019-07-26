package com.github.lit.support.data.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class Page<T> {

    @Getter
    @Setter
    private List<T> data;

    @Getter
    @Setter
    private PageInfo pageInfo;

    private Map<String, Object> additional;

    public Page(List<T> data, PageInfo pageInfo) {
        this.data = data;
        this.pageInfo = pageInfo;
    }

    public static <E> Page<E> emptyPage() {
        return new Page<>(Collections.emptyList() , PageInfo.EMPTY);
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
