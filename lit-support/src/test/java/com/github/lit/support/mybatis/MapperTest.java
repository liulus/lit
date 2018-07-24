package com.github.lit.support.mybatis;

import com.github.lit.support.model.Goods;
import com.github.lit.support.model.GoodsCondition;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User : liulu
 * Date : 2018/7/11 19:30
 * version $Id: MapperTest.java, v 0.1 Exp $
 */
public class MapperTest {

    private GoodsMapper goodsMapper;

    @Before
    public void before() {
        SqlSessionFactory sessionFactory = MybatisConfig.getSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession();
        goodsMapper = sqlSession.getMapper(GoodsMapper.class);
    }

    @Test
    public void test() throws Exception {
        Goods goods = new Goods();
        goods.setId(4L);
        goods.setCode("123");
        goods.setFullName("fullName");
//        goods.setPrice(12.34);
        goods.setCreatedAt(new Date());

        goodsMapper.insert(goods);

    }

    @Test
    public void test2() {
        try {
            goodsMapper.delete(1L);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        try {
            goodsMapper.selectById(1L);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    @Test
    public void test3() {

        GoodsCondition condition = new GoodsCondition();
        condition.setId(2L);
        condition.setCodes(Arrays.asList("12", "13"));
        condition.setFullName("2312312");
        condition.setPrice(12.3);

        goodsMapper.selectByCondition(condition);

    }

    @Test
    public void test4() {
        Goods goods = goodsMapper.selectByProperty(Goods::getCode, "2332");
        List<Goods> goods1 = goodsMapper.selectListByProperty(Goods::getCode, "2e3");
    }



}
