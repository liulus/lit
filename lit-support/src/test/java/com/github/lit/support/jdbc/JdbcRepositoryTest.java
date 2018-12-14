package com.github.lit.support.jdbc;

import com.github.lit.support.model.Goods;
import com.github.lit.support.model.GoodsCondition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.Arrays;
import java.util.List;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-11 21:37
 */
public class JdbcRepositoryTest {

    private JdbcRepository jdbcRepository;

    @Before
    public void before () {

        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:mysql://10.10.100.56:3306/qianniu3_dev_20180912");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        NamedParameterJdbcOperations jdbcOperations = new NamedParameterJdbcTemplate(dataSource);

        jdbcRepository = new JdbcRepositoryImpl(jdbcOperations);
    }


    @Test
    public void test1() {
        Goods goods = jdbcRepository.selectById(Goods.class, 20L);
        System.out.println(goods);
    }

    @Test
    public void test2() {

        GoodsCondition condition = new GoodsCondition();
        List<String> codes = Arrays.asList("r1Yn88qdkA2b2pNg", "UjgqfxoepdTkPlQZ");
        condition.setFullName("%哈%");
//        condition.setCodes(codes);

        List<Goods> goods = jdbcRepository.selectList(Goods.class, condition);
        System.out.println(goods);
    }
}
