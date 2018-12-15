package com.github.lit.support.common.page;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
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
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1110752537451926789L;

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
