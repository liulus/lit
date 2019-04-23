package com.github.lit.support.page;

import com.github.lit.support.util.BeanUtils;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-22 12:53
 */
public abstract class PageUtils {


    public static  <S, T> PageResult<T> convert(PageResult<S> sPage, Class<T> tClass) {
        return convert(sPage, tClass, null);
    }

    public static  <S, T> PageResult<T> convert(PageResult<S> sPage, Class<T> tClass, BiConsumer<S, T> consumer) {
        PageResult<T> result = new PageResult<>();
        result.setPageInfo(sPage.getPageInfo());
        result.addAll(sPage.getAdditional());

        List<T> tList = BeanUtils.convertList(tClass, sPage.getData(), consumer);
        result.setData(tList);

        return result;
    }


}
