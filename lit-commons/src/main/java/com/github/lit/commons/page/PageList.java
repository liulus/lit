package com.github.lit.commons.page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User : liulu
 * Date : 2017-2-17 19:43
 * version $Id: PageList.java, v 0.1 Exp $
 */
@Getter
@Setter
@NoArgsConstructor
public class PageList<E> extends ArrayList<E> {

    private static final long serialVersionUID = -3248132653480964900L;

    private PageInfo pageInfo;

    public PageList(Collection<? extends E> c) {
        super(c);
    }

    public PageList(int initialCapacity) {
        super(initialCapacity);
    }

    public PageList(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public PageList(Collection<? extends E> c, PageInfo pageInfo) {
        super(c);
        this.pageInfo = pageInfo;
    }

    public PageList(PageInfo pageInfo, int initialCapacity) {
        super(initialCapacity);
        this.pageInfo = pageInfo;
    }

    public PageList(Collection<? extends E> c, int pageSize, int pageNum) {
        super(c);
        this.pageInfo = new PageInfo(pageSize, pageNum);
    }

    public PageList(int pageSize, int pageNum, int totalRecord) {
        super(pageSize);
        this.pageInfo = new PageInfo(pageSize, pageNum, totalRecord);
    }

    public PageList(Collection<? extends E> c, int pageSize, int pageNum, int totalRecord) {
        super(c);
        this.pageInfo = new PageInfo(pageSize, pageNum, totalRecord);
    }

}
