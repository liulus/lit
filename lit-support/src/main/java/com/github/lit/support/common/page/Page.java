package com.github.lit.support.common.page;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-15 13:41
 */
@ToString
public class Page<T>  {

    @Getter
    @Setter
    private Collection<T> result;

    @Getter
    @Setter
    private PageInfo pageInfo;

    private Map<String, Object> additional;


    public void add(String key, String object) {
        if (additional == null) {
            additional = new HashMap<>();
        }
        additional.put(key, object);
    }

    public Map<String, Object> getAdditional() {
        if (additional == null) {
            return null;
        }
        return Collections.unmodifiableMap(additional);
    }


}
