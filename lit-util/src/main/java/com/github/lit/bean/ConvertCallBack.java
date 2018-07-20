package com.github.lit.bean;

/**
 * User : liulu
 * Date : 2017-2-19 19:51
 * version $Id: ConvertCallBack.java, v 0.1 Exp $
 */
public interface ConvertCallBack<S, T> {

    void convertCallBack(S source, T target);

}
