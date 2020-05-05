package com.lit.support.data.domain;

import com.lit.support.util.bean.BeanUtils;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-22 12:53
 */
public abstract class PageUtils {


    public static  <S, T> Page<T> convert(Page<S> sPage, Class<T> tClass) {
        return convert(sPage, tClass, null);
    }

    public static  <S, T> Page<T> convert(Page<S> sPage, Class<T> tClass, BiConsumer<S, T> consumer) {
        Page<T> result = new Page<>();
        result.setPageInfo(sPage.getPageInfo());
        result.addAll(sPage.getAdditional());

        List<T> tList = BeanUtils.convertList(tClass, sPage.getData(), consumer);
        result.setData(tList);

        return result;
    }


}
