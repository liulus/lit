package com.github.lit.commons.page;

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
public class Page implements Serializable {

    private static final long serialVersionUID = -2502137541842239335L;

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

    @Getter
    protected String keyWord;

    public Page(int pageSize, int pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    public Page(int pageSize, int pageNum, boolean count) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.count = count;
    }

    public String getBlurKeyWord() {
        return "%" + keyWord + "%";
    }

    public int getOffset() {
        return getPageSize() * (getPageNum() - 1);
    }

    public int getPageSize() {
        return pageSize < 1 ? 20 : pageSize;
    }

    public int getPageNum() {
        return pageNum < 1 ? 1 : pageNum;
    }

}
