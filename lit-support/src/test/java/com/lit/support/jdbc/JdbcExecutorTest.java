package com.lit.support.jdbc;

import com.lit.support.configure.SpringTestConfigure;
import com.lit.support.data.SQL;
import com.lit.support.page.Page;
import com.lit.support.page.PageInfo;
import com.lit.support.page.Sort;
import com.lit.support.data.TableMetaDate;
import com.lit.support.data.jdbc.JdbcExecutor;
import com.lit.support.model.ProductCondition;
import com.lit.support.model.SignProduct;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-11 21:37
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringTestConfigure.class)
@Sql(scripts = "/sql/init_schema.sql")
public class JdbcExecutorTest {

    @Resource
    private JdbcExecutor jdbcExecutor;

    @Test
    public void insert() {
        SignProduct signProduct = new SignProduct();
        signProduct.setCode("826478");
        signProduct.setFullName("签约产品一号");
        signProduct.setInventory(826);
        jdbcExecutor.insert(signProduct);

        Assert.assertTrue(signProduct.getId() >= 1);
    }

    @Test
    public void batchInsert() {

        List<SignProduct> productList = Stream.of("123", "456", "789", "012", "345")
                .map(s -> {
                    SignProduct signProduct = new SignProduct();
                    signProduct.setCode(s);
                    return signProduct;
                }).collect(Collectors.toList());
        int insert = jdbcExecutor.batchInsert(productList);

        Assert.assertEquals(productList.size(), insert);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void update() {
        String upCode = "new_code";
        String upName = "new_full_name";

        SignProduct old = jdbcExecutor.selectById(SignProduct.class, 1L);
        Assert.assertNotNull(old);
        Assert.assertNotEquals(old.getCode(), upCode);
        Assert.assertNotEquals(old.getFullName(), upName);

        SignProduct upProduct = new SignProduct();
        upProduct.setId(old.getId());
        upProduct.setCode(upCode);
        upProduct.setFullName(upName);
        jdbcExecutor.update(upProduct);

    }

    @Test
    public void updateSelective() {
        String upCode = "new_code";
        String upName = "new_full_name";

        SignProduct old = jdbcExecutor.selectById(SignProduct.class, 1L);
        Assert.assertNotNull(old);
        Assert.assertNotEquals(old.getCode(), upCode);
        Assert.assertNotEquals(old.getFullName(), upName);

        SignProduct upProduct = new SignProduct();
        upProduct.setId(old.getId());
        upProduct.setCode(upCode);
        upProduct.setFullName(upName);
        jdbcExecutor.updateSelective(upProduct);

        SignProduct newProduct = jdbcExecutor.selectById(SignProduct.class, 1L);
        Assert.assertNotNull(newProduct);
        Assert.assertEquals(newProduct.getCode(), upCode);
        Assert.assertEquals(newProduct.getFullName(), upName);
    }

    @Test
    public void delete() {
        SignProduct deleteProduct = new SignProduct();
        deleteProduct.setId(1L);
        int deleted = jdbcExecutor.delete(deleteProduct);
        Assert.assertTrue(deleted >= 1);

        SignProduct signProduct = jdbcExecutor.selectById(SignProduct.class, 1L);
        Assert.assertNull(signProduct);
    }

    @Test
    public void deleteById() {
        int deleted = jdbcExecutor.deleteById(SignProduct.class, 1L);
        Assert.assertEquals(1, deleted);

        SignProduct signProduct = jdbcExecutor.selectById(SignProduct.class, 1L);
        Assert.assertNull(signProduct);
    }

    @Test
    public void deleteByIds() {
        int deleted = jdbcExecutor.deleteByIds(SignProduct.class, Arrays.asList(1L, 2L));
        Assert.assertEquals(2, deleted);

        SignProduct signProduct = jdbcExecutor.selectById(SignProduct.class, 1L);
        Assert.assertNull(signProduct);

        signProduct = jdbcExecutor.selectById(SignProduct.class, 2L);
        Assert.assertNull(signProduct);
    }


    @Test
    public void selectById() {
        SignProduct signProduct = jdbcExecutor.selectById(SignProduct.class, 1L);
        Assert.assertNotNull(signProduct);
    }

    @Test
    public void selectByIds() {
        List<SignProduct> signProducts = jdbcExecutor.selectByIds(SignProduct.class, Arrays.asList(1L, 2L));
        Assert.assertEquals(2, signProducts.size());
    }


    @Test
    public void selectAll() {
        List<SignProduct> signProducts = jdbcExecutor.selectAll(SignProduct.class);
        TableMetaDate mataDate = TableMetaDate.forClass(SignProduct.class);
        SQL sql = SQL.init().SELECT("count(*)").FROM(mataDate.getTableName());
        int count = jdbcExecutor.selectForObject(sql, null, int.class);
        Assert.assertEquals(count, signProducts.size());
    }

    @Test
    public void selectByProperty() {
        SignProduct signProduct = jdbcExecutor.selectByProperty(SignProduct::getCode, "893341");

        Assert.assertNotNull(signProduct);
    }

    @Test
    public void selectListByProperty() {
        List<SignProduct> signProducts = jdbcExecutor.selectListByProperty(SignProduct::getCode, "893341");
        Assert.assertEquals(1, signProducts.size());
    }

    @Test
    public void selectForObject() {
        TableMetaDate mataDate = TableMetaDate.forClass(SignProduct.class);
        SQL sql = SQL.init().SELECT("count(*)")
                .FROM(mataDate.getTableName())
                .WHERE("code = :code");
        int count = jdbcExecutor.selectForObject(sql, Collections.singletonMap("code", "893341"), int.class);
        Assert.assertEquals(1, count);
    }

    @Test
    public void selectList() {

        ProductCondition condition = new ProductCondition();
        List<String> codes = Arrays.asList("893341", "213324", "123456");
        condition.setCodes(codes);

        List<SignProduct> signProducts = jdbcExecutor.selectList(SignProduct.class, condition);
        signProducts.forEach(signProduct -> Assert.assertTrue(codes.contains(signProduct.getCode())));

        condition.setFullName("%测试%");
        signProducts = jdbcExecutor.selectList(SignProduct.class, condition);
        signProducts.forEach(signProduct -> {
            Assert.assertTrue(codes.contains(signProduct.getCode()));
            Assert.assertTrue(signProduct.getFullName(). contains("测试"));
        });
    }

    @Test
    public void selectListWithOrder() {

        ProductCondition condition = new ProductCondition();
        Sort orderBy = Sort.init().asc(SignProduct::getCode).desc(SignProduct::getGmtCreate);
        List<SignProduct> signProducts = jdbcExecutor.selectListWithOrder(SignProduct.class, condition, orderBy);

        SignProduct last = null;
        for (SignProduct signProduct : signProducts) {
            if (last == null) {
                last = signProduct;
                continue;
            }
            Assert.assertTrue(last.getCode().compareTo(signProduct.getCode()) < 0);
            if (Objects.equals(last.getCode(), signProduct.getCode())) {
                Assert.assertTrue(last.getGmtCreate().compareTo(signProduct.getGmtCreate()) >= 0);
            }
        }
    }

    @Test
    public void selectForList() {
        TableMetaDate mataDate = TableMetaDate.forClass(SignProduct.class);
        SQL sql = SQL.init().SELECT(mataDate.getColumn(SignProduct::getCode))
                .FROM(mataDate.getTableName())
                .ORDER_BY(mataDate.getColumn(SignProduct::getCode) + " desc");

        List<String> codes = jdbcExecutor.selectForList(sql, null, String.class);

        String last = "";
        for (String code : codes) {
            if (StringUtils.hasText(last)) {
                Assert.assertTrue(last.compareTo(code) >= 0);
            }
        }
    }

    @Test
    public void selectPageList() {
        ProductCondition condition = new ProductCondition();
        condition.setPageSize(2);
        Page<SignProduct> signProducts = jdbcExecutor.selectPageList(SignProduct.class, condition);
        PageInfo pageInfo = signProducts.getPageInfo();
        Assert.assertEquals(2, pageInfo.getPageSize());
        Assert.assertEquals(condition.getPageNum(), pageInfo.getPageNum());
        Assert.assertTrue(pageInfo.getTotal() >= 2);
        Assert.assertEquals(2, signProducts.getData().size());

    }

    @Test
    public void countByProperty() {
        int count = jdbcExecutor.countByProperty(SignProduct::getCode, "123123");
        Assert.assertEquals(0, count);
    }



}
