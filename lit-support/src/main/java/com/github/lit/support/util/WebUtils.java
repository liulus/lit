package com.github.lit.support.util;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * User : liulu
 * Date : 2017/8/9 21:22
 * version $Id: WebUtils.java, v 0.1 Exp $
 */
public abstract class WebUtils {


    private static ServletContext SERVLET_CONTEXT;


    public static void setServletContext(ServletContext servletContext) {
        if (SERVLET_CONTEXT == null) {
            SERVLET_CONTEXT = servletContext;
        }
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static Object getSessionAttribute(String name){
        return getSession().getAttribute(name);
    }

    public static void setSessionAttribute(String name, Object object){
        getSession().setAttribute(name, object);
    }

    public static void removeSessionAttribute(String name) {
        getSession().removeAttribute(name);
    }


    public static ServletContext getServletContext() {

        if (SERVLET_CONTEXT == null) {
            ApplicationContext applicationContext = SpringContextUtils.getApplicationContext();
            if (applicationContext instanceof WebApplicationContext) {
                SERVLET_CONTEXT = ((WebApplicationContext) applicationContext).getServletContext();
            }
        }

        if (SERVLET_CONTEXT == null) {
            SERVLET_CONTEXT = getSession().getServletContext();
        }

        return SERVLET_CONTEXT;
    }

    public static Object getContextAttribute(String name) {

        return getServletContext().getAttribute(name);
    }

    public static void setContextAttribute(String name, Object object) {
        getServletContext().setAttribute(name, object);
    }

    public static void removeContextAttribute(String name) {
        getServletContext().removeAttribute(name);
    }
}
