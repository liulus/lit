package com.lit.support.page;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * User : liulu
 * Date : 2016-10-5 11:03
 */
@Setter
@NoArgsConstructor
public class PageRequest implements Pageable, Serializable {

    private static final long serialVersionUID = -2502137541842239335L;

    private static final int MAX_PAGE_SIZE = 200;

    private transient Sort sort;

    /**
     * 每页记录数
     */
    protected int pageSize = 20;

    /**
     * 当前页
     */
    protected int pageNum = 1;

    /**
     * 是否查询总记录数
     */
    protected boolean count = true;


    public PageRequest(int pageSize, int pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    public PageRequest(int pageSize, int pageNum, boolean count) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.count = count;
    }

    @Override
    public int getOffset() {
        return getPageSize() * (getPageNum() - 1);
    }

    @Override
    public int getPageSize() {
        return Math.min(MAX_PAGE_SIZE, Math.max(1, pageSize));
    }

    @Override
    public int getPageNum() {
        return Math.max(1, pageNum);
    }

    @Override
    public boolean isCount() {
        return count;
    }

    @Override
    public Sort getSort() {
        return sort;
    }


}
