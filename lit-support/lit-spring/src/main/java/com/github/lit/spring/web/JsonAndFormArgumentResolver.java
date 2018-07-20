package com.github.lit.spring.web;

import com.github.lit.spring.util.WebUtils;
import lombok.Setter;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;

/**
 * User : liulu
 * Date : 2018/4/4 09:50
 * version $Id: JsonAndFormArgumentResolver.java, v 0.1 Exp $
 */
public class JsonAndFormArgumentResolver implements HandlerMethodArgumentResolver {

    @Setter
    private RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor;

    @Setter
    private ModelAttributeMethodProcessor modelAttributeMethodProcessor;


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return modelAttributeMethodProcessor.supportsParameter(parameter)
                || requestResponseBodyMethodProcessor.supportsParameter(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = WebUtils.getRequest();
        if (HttpMethod.GET.matches(request.getMethod().toUpperCase())) {
            return modelAttributeMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }
        if (request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
            return requestResponseBodyMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }
        return modelAttributeMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }
}
