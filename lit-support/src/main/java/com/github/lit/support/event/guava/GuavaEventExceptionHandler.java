package com.github.lit.support.event.guava;

import com.github.lit.support.exception.BizException;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User : liulu
 * Date : 2018/4/16 11:16
 * version $Id: EventExceptionHandler.java, v 0.1 Exp $
 */
public class GuavaEventExceptionHandler implements SubscriberExceptionHandler {

    private static final ThreadLocal<BizException> BIZ_EXCEPTION = new ThreadLocal<>();

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        Logger logger = logger(context);
        if (logger.isLoggable(Level.SEVERE)) {
            if (exception instanceof BizException) {
                logger.log(Level.SEVERE, message(context));
            } else {
                logger.log(Level.SEVERE, message(context), exception);
            }
        }
        if (exception instanceof BizException) {
            BIZ_EXCEPTION.set((BizException) exception);
        }
    }

    public static BizException getBizException() {
        return BIZ_EXCEPTION.get();
    }

    public static void clearBizException() {
        BIZ_EXCEPTION.remove();
    }


    private static Logger logger(SubscriberExceptionContext context) {
        return Logger.getLogger(EventBus.class.getName() + "." + context.getEventBus().identifier());
    }

    private static String message(SubscriberExceptionContext context) {
        Method method = context.getSubscriberMethod();
        return "Exception thrown by subscriber method "
                + method.getName()
                + '('
                + method.getParameterTypes()[0].getName()
                + ')'
                + " on subscriber "
                + context.getSubscriber()
                + " when dispatching event: "
                + context.getEvent();
    }
}
