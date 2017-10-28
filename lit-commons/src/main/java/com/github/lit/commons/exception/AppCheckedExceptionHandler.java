package com.github.lit.commons.exception;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User : liulu
 * Date : 2017/3/20 21:05
 * version $Id: AppExceptionHandler.java, v 0.1 Exp $
 */
@Slf4j
@Aspect
@NoArgsConstructor
public class AppCheckedExceptionHandler {

    private static ThreadLocal<AtomicInteger> methodHierarchy = new ThreadLocal<>();

    /**
     * 执行时间超过打印warn日志毫秒数
     */
    private static final long LOG_TIMEOUT = 1000;

    @Pointcut("@within(org.springframework.stereotype.Service)")
    private void annotationPointCut() {
    }

    @Pointcut("bean(*serviceImpl)")
    private void beanNamePointCut() {
    }

    /**
     * 拦截业务方法
     *
     * @param pjp
     * @return
     */
    @Around("annotationPointCut() || beanNamePointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        AtomicInteger ai = methodHierarchy.get();
        if (ai == null) {
            ai = new AtomicInteger(1);
            methodHierarchy.set(ai);
        } else {
            ai.incrementAndGet();
        }

        //被拦截的类
        String targetClass = pjp.getTarget().getClass().getName();

        //被拦截方法
        Signature signature = pjp.getSignature();
        String targetMethod = signature.getName();

        //当前时间毫秒数
        long beginTime = System.currentTimeMillis();

        log.debug("start:[class={}, method={}, beginTime={}]", targetClass, targetMethod, beginTime);

        Object result;
        try {
            result = pjp.proceed();
        } catch (Throwable throwable) {
            if (ai.get() > 1) {
                throw throwable;
            }
            AppExceptionTransactionAspectSupport.rollBack();

            if (throwable instanceof AppCheckedException) {
                log.warn("checked exception: [method={}#{}], errorCode={} errorMsg={}], [args={}]",
                        targetClass, targetMethod, ((AppCheckedException) throwable).getErrorCode(), throwable.getMessage(), argsToString(pjp));
            } else {
                RunResultHolder.addError("系统未知异常");
                log.warn(String.format("unchecked exception: [method=%s#%s], [errorMsg=%s], [args=%s]",
                        targetClass, targetMethod, throwable.getMessage(), argsToString(pjp)), throwable);
                throw throwable;
            }

            result = getDefaultValue(signature);
        } finally {
            methodHierarchy.remove();
        }

        long endTime = System.currentTimeMillis();
        long useTime = endTime - beginTime;

        log.debug("end:[class={},method={},endTime={},usedTime={}]", targetClass, targetMethod, endTime, useTime);

        if (useTime > LOG_TIMEOUT) {
            log.warn("Long processing time:[class={},method={},usedTime={}]", targetClass, targetMethod, useTime);
        }
        return result;
    }

    /**
     * 回滚事务
     */
    static class AppExceptionTransactionAspectSupport extends TransactionAspectSupport {

        static void rollBack() {
            TransactionInfo transactionInfo = currentTransactionInfo();
            if (transactionInfo != null && transactionInfo.hasTransaction()) {
                TransactionStatus transactionStatus = transactionInfo.getTransactionStatus();
                if (transactionStatus != null) {
                    transactionStatus.setRollbackOnly();
                }
            }
        }
    }

    /**
     * 获取基本类型的默认值
     * 如果方法返回的是基本的值类型,直接返回null会出异常
     *
     * @param signature
     * @return
     */
    private Object getDefaultValue(Signature signature) {
        if (!(signature instanceof MethodSignature)) {
            return null;
        }

        MethodSignature methodSignature = (MethodSignature) signature;
        Class<?> returnType = methodSignature.getReturnType();
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (returnType == Boolean.TYPE) {
            return Boolean.FALSE;
        } else if (returnType == Character.TYPE) {
            return '\u0000';
        } else if (returnType == Byte.TYPE) {
            return (byte) 0;
        } else if (returnType == Short.TYPE) {
            return (short) 0;
        } else if (returnType == Integer.TYPE) {
            return 0;
        } else if (returnType == Long.TYPE) {
            return 0L;
        } else if (returnType == Float.TYPE) {
            return 0.0F;
        } else if (returnType == Double.TYPE) {
            return 0.0D;
        }
        return null;
    }

    /**
     * 获取参数字符串
     *
     * @param pjp
     * @return
     */
    private String argsToString(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        return Arrays.toString(args);
    }


}
