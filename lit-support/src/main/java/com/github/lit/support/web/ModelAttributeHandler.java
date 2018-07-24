package com.github.lit.support.web;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * User : liulu
 * Date : 2018/3/1 11:40
 * version $Id: ModelAttributeHandler.java, v 0.1 Exp $
 */
public class ModelAttributeHandler implements HandlerMethodReturnValueHandler {


    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return true;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.addAttribute("data", returnValue);
    }
}
