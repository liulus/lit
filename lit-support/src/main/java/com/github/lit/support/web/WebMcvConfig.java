package com.github.lit.support.web;

import com.github.lit.support.event.guava.EnableGuavaEvent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;
import java.util.List;

/**
 * User : liulu
 * Date : 2018/4/16 18:05
 * version $Id: WebMcvConfig.java, v 0.1 Exp $
 */
@EnableCaching
@EnableGuavaEvent
@ComponentScan("com.github.lit.support")
public class WebMcvConfig {

    /**
     * 配置多视图解析器
     *
     * @param manager       manager 会自动构建，configureContentNegotiation可以进行配置
     * @param viewResolvers 当前项目的 viewResolver, (此时会包含上面配置的 freemarkerViewResolver)
     * @return ContentNegotiatingViewResolver
     * @see WebMvcConfigurerAdapter#configureContentNegotiation(org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer)
     */
    @Bean
    public ContentNegotiatingViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager, List<ViewResolver> viewResolvers) {

        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setContentNegotiationManager(manager);

        View jackson2JsonView = new MappingJackson2JsonView();
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

            @Override
            public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
                configurer.defaultContentType(MediaType.TEXT_HTML);
                configurer.ignoreAcceptHeader(true);
            }
        };
    }

    @Bean
    public BeanPostProcessor jsonAndFormArgumentResolverProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

                if (bean instanceof RequestMappingHandlerAdapter) {
                    JsonAndFormArgumentResolver jsonAndFormArgumentResolver = null;
                    ModelAttributeMethodProcessor modelAttributeMethodProcessor = null;
                    RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor = null;

                    for (HandlerMethodArgumentResolver argumentResolver : ((RequestMappingHandlerAdapter) bean).getArgumentResolvers()) {
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

                return bean;
            }
        };
    }


}
