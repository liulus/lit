package com.github.lit.autoconfigure.mybatis;

import com.github.lit.support.mybatis.plugin.ResultMapInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author liulu
 * @version : v1.0
 * date : 7/24/18 17:49
 */
@Configuration
@ComponentScan("com.github.lit.support")
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class LitMybatisAutoConfigure {

    @Resource
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @PostConstruct
    public void addInterceptor() {
        //
        ResultMapInterceptor resultMapInterceptor = new ResultMapInterceptor();
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(resultMapInterceptor);
        }
    }

}
