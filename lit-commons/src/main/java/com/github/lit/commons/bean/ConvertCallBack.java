package com.github.lit.commons.bean;

/**
 * User : liulu
 * Date : 2017-2-19 19:51
 * version $Id: ConvertCallBack.java, v 0.1 Exp $
 */
public interface ConvertCallBack<T, S> {

    void convertCallBack(T target, S source);

}
