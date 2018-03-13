package com.github.lit.commons.event;

import com.github.lit.commons.bean.BeanUtils;
import com.github.lit.commons.util.ClassUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * User : liulu
 * Date : 2017/8/9 22:00
 * version $Id: PublishEventAspect.java, v 0.1 Exp $
 */
@Aspect
public class PublishEventAspect {

    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Resource
    private EventPublisher eventPublisher;

    @After("@annotation(event)")
    public void publishEvent(JoinPoint joinPoint, Event event) {

        Class<?> eventClass = event.eventClass();

        Object eventObj = newInstanceAndInitProperty(eventClass, joinPoint);

        if (Objects.equals(event.publishType(), Event.Type.SYNC)) {
            eventPublisher.publish(eventObj);
        } else if (Objects.equals(event.publishType(), Event.Type.ASYNC)) {
            eventPublisher.asyncPublish(eventObj);
        }

    }

    /**
     * 实例化 事件对象 并将切入点方法参数的值注入事件对象的属性中, 规则:
     * 1. 方法参数名和对象属性名一致 且 事件对象属性的类型和参数类型一致或是其父类, 直接注入
     * 2. 非基本类型或其包装类, 参数类型中只有一个和属性类型一致或是其子类的, 注入
     *
     * @param eventClass 事件对象
     * @param joinPoint  切入点
     * @return 事件对象
     */
    private Object newInstanceAndInitProperty(Class<?> eventClass, JoinPoint joinPoint) {

        Object eventObj = ClassUtils.newInstance(eventClass);

        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();

        String[] parameterNames = parameterNameDiscoverer.getParameterNames(targetMethod);
        Object[] args = joinPoint.getArgs();
        Class<?>[] parameterTypes = targetMethod.getParameterTypes();

        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(eventClass);
        for (PropertyDescriptor descriptor : descriptors) {

            Class<?> propertyType = descriptor.getPropertyType();

            int nameIndex = getParameterNameIndex(parameterNames, descriptor.getName());
            // 名称一致, 是子类或相同
            if (nameIndex >= 0 && propertyType.isAssignableFrom(parameterTypes[nameIndex])) {
                ClassUtils.invokeMethod(descriptor.getWriteMethod(), eventObj, args[nameIndex]);
            } else if (!ClassUtils.isPrimitiveOrWrapper(propertyType)) {
                int typeIndex = getParameterTypeIndex(parameterTypes, propertyType);
                if (typeIndex >= 0) {
                    ClassUtils.invokeMethod(descriptor.getWriteMethod(), eventObj, args[typeIndex]);
                }
            }
        }
        return eventObj;
    }

    private int getParameterNameIndex(String[] parameterNames, String searchName) {
        for (int i = 0; i < parameterNames.length; i++) {
            if (Objects.equals(parameterNames[i], searchName)) {
                return i;
            }
        }
        return -1;
    }

    private int getParameterTypeIndex(Class<?>[] parameterTypes, Class<?> searchType) {
        int result = -1, count = 0;
        for (int i = 0; i < parameterTypes.length; i++) {
            if (searchType.isAssignableFrom(parameterTypes[i])) {
                result = i;
                count++;
            }
        }
        return count > 1 ? -1 : result;
    }

}
