package com.github.lit.support.page;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-21 18:58
 */
public interface Pageable {


    int getPageNum();

    int getPageSize();

    int getOffset();

    OrderBy getOrderBy();

    default boolean isCount() {
        return true;
    }


}
