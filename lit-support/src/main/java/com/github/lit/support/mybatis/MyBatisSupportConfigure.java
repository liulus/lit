package com.github.lit.support.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-11 19:49
 */
public class MyBatisSupportConfigure {

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
