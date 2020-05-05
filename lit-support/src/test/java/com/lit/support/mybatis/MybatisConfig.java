package com.lit.support.mybatis;

import com.lit.support.data.mybatis.ResultMapInterceptor;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class MybatisConfig {

    private static SqlSessionFactory sessionFactory;

    public static SqlSessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            PooledDataSource dataSource = getDataSource();
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("development", transactionFactory, dataSource);
            Configuration configuration = new Configuration(environment);
            configuration.addInterceptor(new ResultMapInterceptor());

            configuration.addMapper(GoodsMapper.class);

            sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        }
        return sessionFactory;
    }

    private static PooledDataSource getDataSource() {
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://10.10.100.56:3306/qn_dev");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        return dataSource;
    }

}
