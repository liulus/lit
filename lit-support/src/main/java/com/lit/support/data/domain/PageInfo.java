package com.lit.support.data.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * User : liulu
 * Date : 2017/3/20 19:30
 * version $Id: PageInfo.java, v 0.1 Exp $
 */
@Data
@NoArgsConstructor
public class PageInfo implements Serializable {

    private static final long serialVersionUID = -1073813980324211986L;

    private static final int PAGER_LENGTH = 3;

    public static final PageInfo EMPTY = new PageInfo();

    /**
     * 每页记录数
     */
    private int pageSize;

    /**
     * 当前页
     */
    private int pageNum;

    /**
     * 总记录数
     */
    private int total;

    public PageInfo(int pageSize, int pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    public PageInfo(int pageSize, int pageNum, int total) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.total = total;
    }

    public int getPageNum() {
        return Math.min(Math.max(1, pageNum), getTotalPage());
    }

    /**
     * @return 总页数
     */
    public int getTotalPage() {
        return (total - 1) / pageSize + 1;
    }

    /**
     * @return 分页条起始页码
     */
    public int getStart() {
        return Math.max(1, Math.min(getPageNum(), getTotalPage() - PAGER_LENGTH) - PAGER_LENGTH);
    }

    /**
     * @return 分页条结束页码
     */
    public int getEnd() {
        return Math.min(getTotalPage(), Math.max(getPageNum(), PAGER_LENGTH + 1) + PAGER_LENGTH);
    }

    public boolean isFirstPage() {
        return getPageNum() == 1;
    }

    public boolean isLastPage() {
        return Objects.equals(getTotalPage(), getPageNum());
    }

}
