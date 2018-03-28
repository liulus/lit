package com.github.lit.test.config;

import com.github.lit.jdbc.spring.config.EnableLitJdbc;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * User : liulu
 * Date : 2017-2-21 17:49
 * version $Id: SpringConfig.java, v 0.1 Exp $
 */

@Configuration
@PropertySource("config.properties")
@EnableLitJdbc
public class SpringConfig {

    private static final String DB = "mysql.";

    @Resource
    private Environment env;

    @Bean
    public DataSource mysqlDataSource() throws PropertyVetoException {

        ComboPooledDataSource source = new ComboPooledDataSource();
        source.setJdbcUrl(env.getProperty(DB + "url"));
        source.setDriverClass(env.getProperty(DB + "driver"));
        source.setUser(env.getProperty(DB + "user"));
        source.setPassword(env.getProperty(DB + "password"));
        source.setMinPoolSize(Integer.valueOf(env.getProperty("pool.minPoolSize")));
        source.setMaxPoolSize(Integer.valueOf(env.getProperty("pool.maxPoolSize")));
        source.setAutoCommitOnClose(false);
        source.setCheckoutTimeout(Integer.valueOf(env.getProperty("pool.checkoutTimeout")));
        source.setAcquireRetryAttempts(2);

        return source;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JdbcOperations jdbcOperations(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


}
