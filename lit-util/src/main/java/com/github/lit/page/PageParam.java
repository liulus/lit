package com.github.lit.page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * User : liulu
 * Date : 2016-10-5 11:03
 */
@Setter
@NoArgsConstructor
public class PageParam implements Serializable {

    private static final long serialVersionUID = -2502137541842239335L;

    private static final int MAX_PAGE_SIZE = 200;

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
    @Getter
    protected boolean count = true;


    public PageParam(int pageSize, int pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    public PageParam(int pageSize, int pageNum, boolean count) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.count = count;
    }

    public int getOffset() {
        return getPageSize() * (getPageNum() - 1);
    }

    public int getPageSize() {
        return Math.min(MAX_PAGE_SIZE, Math.max(1, pageSize));
    }

    public int getPageNum() {
        return Math.max(1, pageNum);
    }

}
