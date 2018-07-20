package com.github.lit.spring.web;

import com.github.lit.spring.event.guava.EnableGuavaEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * User : liulu
 * Date : 2018/4/16 18:05
 * version $Id: WebMcvConfig.java, v 0.1 Exp $
 */
@EnableCaching
@EnableGuavaEvent
@ComponentScan("com.github.lit.spring")
public class WebMcvConfig {

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @PostConstruct
    public void initJsonAndFormArgumentResolver() {

        JsonAndFormArgumentResolver jsonAndFormArgumentResolver = null;
        ModelAttributeMethodProcessor modelAttributeMethodProcessor = null;
        RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor = null;

        for (HandlerMethodArgumentResolver argumentResolver : requestMappingHandlerAdapter.getArgumentResolvers()) {
            if (argumentResolver instanceof JsonAndFormArgumentResolver) {
                jsonAndFormArgumentResolver = (JsonAndFormArgumentResolver) argumentResolver;
                continue;
            }
            if (argumentResolver instanceof RequestResponseBodyMethodProcessor) {
                requestResponseBodyMethodProcessor = (RequestResponseBodyMethodProcessor) argumentResolver;
                continue;
            }
            if (argumentResolver instanceof ModelAttributeMethodProcessor) {
                modelAttributeMethodProcessor = (ModelAttributeMethodProcessor) argumentResolver;
            }
        }
        if (jsonAndFormArgumentResolver != null) {
            jsonAndFormArgumentResolver.setModelAttributeMethodProcessor(modelAttributeMethodProcessor);
            jsonAndFormArgumentResolver.setRequestResponseBodyMethodProcessor(requestResponseBodyMethodProcessor);
        }
    }

    @Bean
    public ContentNegotiatingViewResolver contentNegotiatingViewResolver(List<ViewResolver> viewResolvers) {

        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();

        ContentNegotiationManagerFactoryBean bean = new ContentNegotiationManagerFactoryBean();
        bean.setIgnoreAcceptHeader(true);
        bean.setDefaultContentType(MediaType.TEXT_HTML);

        View jackson2JsonView = new MappingJackson2JsonView();

        viewResolver.setContentNegotiationManager(bean.getObject());
        viewResolver.setDefaultViews(Collections.singletonList(jackson2JsonView));
        viewResolver.setViewResolvers(viewResolvers);

        return viewResolver;
    }


    @Bean
    public WebMvcConfigurerAdapter myWebMvcConfigurerAdapter() {
        return new WebMvcConfigurerAdapter() {

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new ModelAttributeInterceptor()).addPathPatterns("/**");
            }

            /**
             * 自定义 controller 方法返回值处理
             *
             * @param returnValueHandlers returnValueHandlers
             */
            @Override
            public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
                returnValueHandlers.add(new ModelAttributeHandler());
            }

            /**
             * 自定义参数解析器, 可以根据 content-type 自动解析 request body 中的 json绑定参数
             *
             * @param argumentResolvers argumentResolvers
             */
            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
                argumentResolvers.add(new JsonAndFormArgumentResolver());
            }
        };
    }


}
