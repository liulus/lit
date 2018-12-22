package com.github.lit.support.web;

import com.github.lit.support.page.PageInfo;
import com.github.lit.support.page.PageList;
import com.github.lit.support.util.SpelUtils;
import com.github.lit.support.web.annotation.ViewName;
import org.springframework.ui.ModelMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * User : liulu
 * Date : 2017/3/20 20:21
 * version $Id: ModelAttributeInterceptor.java, v 0.1 Exp $
 */
public class ModelAttributeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) {
            return;
        }
        ModelMap modelMap = modelAndView.getModelMap();
        modelMap.put("success", true);

        // 处理自定义视图名称
        if (request.getRequestURI().endsWith(".json")) {
            modelAndView.setViewName("");
        } else if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            ViewName viewName = method.getAnnotation(ViewName.class);
            if (viewName != null) {
                String name = viewName.spel() ? SpelUtils.getExpressionValue(viewName.value(), modelMap) : viewName.value();
                modelAndView.setViewName(name);
            }
        }

        // 处理分页信息
        PageInfo pageInfo = null;
        for (Object obj : modelMap.values()) {
            if (obj instanceof PageList) {
//                pageInfo = ((PageList) obj).getPageInfo();
                break;
            }
        }
        if (pageInfo != null) {
            modelMap.put("PAGE_INFO", pageInfo);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
