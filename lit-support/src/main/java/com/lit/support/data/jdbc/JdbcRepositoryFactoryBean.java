package com.lit.support.data.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;

/**
 * @author liulu
 * @version V1.0
 * @since 2020/9/28
 */
public class JdbcRepositoryFactoryBean<T> implements FactoryBean<T> {

    private Class<T> repositoryInterface;

    @Getter
    @Setter
    private DataSource dataSource;

    public JdbcRepositoryFactoryBean() {
        //intentionally empty
    }

    public JdbcRepositoryFactoryBean(Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
    }

    @Override
    public T getObject() throws Exception {
        JdbcRepositoryProxy jdbcRepositoryProxy = new JdbcRepositoryProxy(repositoryInterface);
        jdbcRepositoryProxy.setDataSource(dataSource);
        Object proxy = (new ProxyFactory(repositoryInterface, jdbcRepositoryProxy)).getProxy(ClassUtils.getDefaultClassLoader());
        return (T) proxy;
    }

    @Override
    public Class<T> getObjectType() {
        return this.repositoryInterface;
    }
}
