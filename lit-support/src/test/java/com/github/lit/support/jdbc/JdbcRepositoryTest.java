package com.github.lit.support.jdbc;

import com.github.lit.support.configure.SpringTestConfigure;
import com.github.lit.support.model.Goods;
import com.github.lit.support.model.GoodsCondition;
import com.github.lit.support.model.SignProduct;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-11 21:37
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringTestConfigure.class)
@Sql(scripts = "/sql/init_schema.sql")
public class JdbcRepositoryTest {

    @Resource
    private JdbcRepository jdbcRepository;

    @Test
    public void insert() {
        SignProduct signProduct = new SignProduct();
        signProduct.setCode("826478");
        signProduct.setFullName("签约产品一号");
        signProduct.setInventory(826);
        jdbcRepository.insert(signProduct);

        Assert.assertTrue(signProduct.getId() >= 1);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void update() {
        String upCode = "new_code";
        String upName = "new_full_name";

        SignProduct old = jdbcRepository.selectById(SignProduct.class, 1L);
        Assert.assertNotNull(old);
        Assert.assertNotEquals(old.getCode(), upCode);
        Assert.assertNotEquals(old.getFullName(), upName);

        SignProduct upProduct = new SignProduct();
        upProduct.setId(old.getId());
        upProduct.setCode(upCode);
        upProduct.setFullName(upName);
        jdbcRepository.update(upProduct);

    }

    @Test
    public void updateSelective() {
        String upCode = "new_code";
        String upName = "new_full_name";

        SignProduct old = jdbcRepository.selectById(SignProduct.class, 1L);
        Assert.assertNotNull(old);
        Assert.assertNotEquals(old.getCode(), upCode);
        Assert.assertNotEquals(old.getFullName(), upName);

        SignProduct upProduct = new SignProduct();
        upProduct.setId(old.getId());
        upProduct.setCode(upCode);
        upProduct.setFullName(upName);
        jdbcRepository.updateSelective(upProduct);

        SignProduct newProduct = jdbcRepository.selectById(SignProduct.class, 1L);
        Assert.assertNotNull(newProduct);
        Assert.assertEquals(newProduct.getCode(), upCode);
        Assert.assertEquals(newProduct.getFullName(), upName);
    }

    @Test
    public void delete() {
        SignProduct deleteProduct = new SignProduct();
        deleteProduct.setId(1L);
        int deleted = jdbcRepository.delete(deleteProduct);
        Assert.assertTrue(deleted >= 1);

        SignProduct signProduct = jdbcRepository.selectById(SignProduct.class, 1L);
        Assert.assertNull(signProduct);
    }

    @Test
    public void deleteById() {
        int deleted = jdbcRepository.deleteById(SignProduct.class, 1L);
        Assert.assertTrue(deleted >= 1);

        SignProduct signProduct = jdbcRepository.selectById(SignProduct.class, 1L);
        Assert.assertNull(signProduct);
    }


    @Test
    public void selectById() {
        SignProduct signProduct = jdbcRepository.selectById(SignProduct.class, 1L);
        Assert.assertNotNull(signProduct);
        Assert.assertEquals("893341", signProduct.getCode());
    }

    @Test
    public void selectAll() {
        List<SignProduct> signProducts = jdbcRepository.selectAll(SignProduct.class);
        Assert.assertEquals(4, signProducts.size());
    }

    @Test
    public void selectByProperty() {
        SignProduct signProduct = jdbcRepository.selectByProperty(SignProduct::getCode, "893341");

        Assert.assertNotNull(signProduct);
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
