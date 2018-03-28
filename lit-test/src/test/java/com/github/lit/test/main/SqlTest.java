package com.github.lit.test.main;

import com.github.lit.jdbc.JdbcTools;
import com.github.lit.test.model.Goods;
import com.github.lit.test.model.Supplier;
import com.github.lit.test.util.DBUtils;
import org.junit.Test;

/**
 * User : liulu
 * Date : 2018/3/13 10:39
 * version $Id: SqlTest.java, v 0.1 Exp $
 */
public class SqlTest {

    private static JdbcTools jdbcTools;

    static {
        jdbcTools = DBUtils.getJdbcTools();
    }

    private Goods goods = Goods.builder().code("00000000").name("test_goods").barCode("61232764726537263")
            .inventory(20).price(19.98).specification("500ml*12").purchaserCode("33018002")
            .supplierCode("00391")
            .goodsId(3L)
            .build();

    @Test
    public void insert() {
        jdbcTools.insert(goods);
    }

    @Test
    public void insert2() {
        jdbcTools.createInsert(Goods.class)
                .set("code", "00000000")
                .set("name", "test_goods")
                .set("price", 19.98)
                .natively().set("purchaser_Code", "'33018002'")
                .execute();
    }

    @Test
    public void update() {
        jdbcTools.update(goods);
    }

    @Test
    public void update2() {
        jdbcTools.createUpdate(Goods.class)
                .set("price", 19.98)
                .set("name", null)
                .and("goodsId").equalsTo(3L)
                .and()
                .bracket("name").like("222")
                .or("inventory").lessThanOrEqual(23)
                .end()
                .execute();
    }

    @Test
    public void delete() {
        jdbcTools.delete(goods);
    }

    @Test
    public void delete2() {
        jdbcTools.createDelete(Goods.class)
                .where("goodsId").equalsTo(3L)
                .execute();
    }

    @Test
    public void select1() {
        jdbcTools.select(Goods.class).tableAlias("lg")
                .include("supplierCode")
                .join(Supplier.class)
                .on(Goods.class, "supplierCode").equalsTo(Supplier.class, "code")
                .tableAlias("ls")
                .where("goodsId").equalsTo(3L)
                .and(Goods.class, "supplierCode").equalsTo(Supplier.class, "code")
                .list();
    }

    @Test
    public void select2() {
        jdbcTools.select(Goods.class).tableAlias("lg")
                .include("supplierCode")
                .function("count").alias("goodsCount")
                .tableAlias("ls")
                .where("goodsId").natively().graterThan(3L)
                .natively()
                .and("price").graterThanOrEqual(10D)
                .groupBy("supplierCode")
                .and("goodsCount").graterThan(100)
                .and("goodsCount").lessThanOrEqual(200)
                .list();
    }


}
