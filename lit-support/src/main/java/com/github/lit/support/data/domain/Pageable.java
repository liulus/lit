package com.github.lit.support.data.domain;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-21 18:58
 */
public interface Pageable {


    int getPageNum();

    int getPageSize();

    int getOffset();

    Sort getSort();

    default boolean isCount() {
        return true;
    }


}
