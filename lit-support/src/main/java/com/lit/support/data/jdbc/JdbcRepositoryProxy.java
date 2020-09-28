package com.lit.support.data.jdbc;

import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.sql.DataSource;

/**
 * @author liulu
 * @version V1.0
 * @since 2020/9/28
 */
public class JdbcRepositoryProxy implements MethodInterceptor {
    @Setter
    private DataSource dataSource;
    public Class<?> iClass;

    public JdbcRepositoryProxy(Class<?> iClass) {
        this.iClass = iClass;
    }


    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String name = methodInvocation.getMethod().getName();

        Object[] arguments = methodInvocation.getArguments();
        System.out.println(arguments.length);

        return 3;
    }
}
