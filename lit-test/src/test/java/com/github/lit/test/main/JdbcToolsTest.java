package com.github.lit.test.main;

import com.github.lit.jdbc.JdbcTools;
import com.github.lit.jdbc.statement.select.Select;
import com.github.lit.support.common.page.PageList;
import com.github.lit.test.base.SpringBaseTest;
import com.github.lit.test.model.Goods;
import com.github.lit.test.model.GoodsVo;
import com.github.lit.test.model.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * User : liulu
 * Date : 2018/3/24 13:08
 * version $Id: JdbcToolsTest.java, v 0.1 Exp $
 */
@Slf4j
public class JdbcToolsTest extends SpringBaseTest {

    @Resource
    private JdbcTools jdbcTools;


    @Test
    public void testInsert1() {
        Goods goods = new Goods();
        goods.setCode("000001");
        goods.setName("哇哈哈矿泉水");
        goods.setPrice(2.5D);
        // ...省略其他set方法

        Long id = jdbcTools.insert(goods);
        log.info("插入的实体ID为: {}", id);
    }

    @Test
    public void testInsert2() {
        Long id = (Long) jdbcTools.createInsert(Goods.class)
                .set("code", "000002")
                .set("name", "哇哈哈矿泉水")
                .set("price", 2D)
                // ...其他属性
                .execute();
        log.info("插入的实体ID为: {}", id);
    }

    @Test
    public void testUpdate1() {

        // update 操作是根据实体的 id 来更新记录, 所以要保证实体中的 Id一定不为空

        Goods goods = new Goods();
        goods.setGoodsId(14378L);
        goods.setName("哇哈哈矿泉水-新包装");
        goods.setPrice(3D);
        // ...其他属性

        // 方式一 实体中值为 null 的属性 不会 更新到数据库中
        jdbcTools.update(goods);

        // 方式二 实体中值为 null 的属性 会 更新到数据库中，
        jdbcTools.update(goods, false);
    }

    @Test
    public void testUpdate2() {

        // 会将 code 为 80145412 的商品的 name 和 price 更新, 返回受影响记录的条数

        int rows = jdbcTools.createUpdate(Goods.class)
                .set("name", "哇哈哈矿泉水-大瓶")
                .set("price", 4.0D)
                .where("code").equalsTo("000001")
                .execute();
    }

    @Test
    public void testDelete() {

        Goods goods = new Goods();
        goods.setGoodsId(1200L);


        // 方式一, 根据实体中的 Id 值删除记录, 返回受影响记录的条数
        int rows = jdbcTools.delete(goods);


        // 方式二, 根据一批 id 值 删除指定的实体记录, 返回受影响记录的条数
        rows = jdbcTools.deleteByIds(Goods.class, 1200L, 1201L);


        // 方式三 删除 code 为 80145412 的商品, 返回受影响记录的条数
        rows = jdbcTools.createDelete(Goods.class)
                .where("code").equalsTo("80145412")
                .execute();
    }

    @Test
    public void testSelect1() {
        // 直接根据 Id 查出对应的实体
        Goods goods = jdbcTools.get(Goods.class, 1203L);
        log.info(goods.toString());
    }

    @Test
    public void testSelect2() {

        // 默认查询实体属性中对应的所有字段
        Select<Goods> select = jdbcTools.select(Goods.class);

        // 使用 include 方法指定查询的字段, 将只查询 code, name, price 对应的字段
        Select<Goods> include = jdbcTools.select(Goods.class)
                .include("code", "name", "price");

        // 使用 exclude 方法指定需要排除查询的字段, 将查询除了 code, name 外所有实体属性中对应的字段
        Select<Goods> exclude = jdbcTools.select(Goods.class)
                .exclude("code", "name");
    }

    @Test
    public void testSelect3() {
        // 1. 指定函数名, 函数指定全部字段, 结果会添加  count(*)
        Select<Goods> count = jdbcTools.select(Goods.class)
                .function("count");

        // 2. 指定函数名和字段, 结果会添加  max(price)
        Select<Goods> maxPrice = jdbcTools.select(Goods.class)
                .function("max", "price");

        // 3. 指定函数名和字段, 第二个参数是 isDistinct, 结果会添加 count( distinct supplier_code )
        Select<Goods> function = jdbcTools.select(Goods.class)
                .function("count", true, "supplierCode");
    }

    @Test
    public void testSelect4() {
        // 当查询列中有函数时, 可以指定别名来和实体属性名称保持一致, 方便获取结果
        // 别名一样对字段也有效果

        // 指定 max(price) 别名为 maxPrice, count( distinct supplier_code ) 别名为 supplierCount
        // 若 XXX 类中有属性 maxPrice, supplierCount, 可以很简单的将查询结果映射为 XXX 对象
        Select<Goods> alias = jdbcTools.select(Goods.class)
                .function("max", "price").alias("maxPrice")
                .function("count", true, "supplierCode").alias("supplierCount");
    }

    @Test
    public void testSelect5() {

        Goods single = jdbcTools.select(Goods.class)
                .join(Supplier.class)
                .additionalField(Supplier.class, "code", "name")
                .alias("supplierCode", "supplierName")
                .on(Goods.class, "supplierCode").equalsTo(Supplier.class, "code")
                .where("goodsId").equalsTo(1203L)
                .single();
    }

    @Test
    public void testSelect6() {

        // 指定条件 code < ? and price > ? and code in (?, ?, ?), ? 参数即为条件逻辑方法中的值
        jdbcTools.select(Goods.class)
                .where("code").lessThan(1123L)
                .and("price").graterThan(548D)
                .and("code").in(1027L, 1078L, 1094L);
    }

    @Test
    public void testSelect7() {

        // 指定条件 code < ? and ( price < ? or category = ? )  ? 参数即为条件逻辑方法中的值
        jdbcTools.select(Goods.class)
                .where("code").lessThan(1123L)
                .and()
                .bracket("price").lessThan(28.8D)
                .or("category").equalsTo("100232")
                .end();
    }

    @Test
    public void testSelect8() {
        // 注: asc 和 desc 方法 可以放多个字段
        jdbcTools.select(Goods.class)
                .where("code").equalsTo("000001")
                .asc("code") // 按 code 升序排列
                .desc("gmtCreate"); // 按创建时间降序排列

    }

    @Test
    public void testSelect9() {
        jdbcTools.select(Goods.class)
                .include("supplierCode")
                .function("count").alias("goodsCount") // count(*) 指定别名 goodsCount
                .function("max", "price").alias("maxPrice") // max(price) 指定别名 maxPrice
                .where("code").like("00%") // 指定查询条件 code like '00%'
                .groupBy("supplierCode") // 按 supplierCode 分组
                .having("goodsCount").graterThan(10) // having 条件 goodsCount > 10
                .and("maxPrice").lessThan(300); // and maxPrice < 300
    }

    @Test
    public void testSelect10() {
        Select<Goods> select = jdbcTools.select(Goods.class)
                .where("code").like("000%");


        // 返回 count
        int count = select.count();


        // 返回 单个 实体对象, 如果查询结果有多条, 会抛出异常, 查询结果为空, 返回 null
        Goods goods = select.single();


        // 返回实体列表
        List<Goods> list = select.list();

        // 返回分页查询结果列表, 可以强转 为 PageList, 获取分页信息
        // 第一个参数是当前页, 第二个参数是 每页大小
        List<Goods> pageList1 = select.page(1, 20).list();

        // 获取分页信息
        PageList<Goods> goodsPage = ((PageList<Goods>) pageList1);


        // 返回分页查询结果列表, 但是 不会查询 count, 只查询分页结果
        List<Goods> pageList2 = select.page(1, 20, false).list();
    }

    @Test
    public void testSelect11() {

        String name = jdbcTools.select(Goods.class)
                .include("name") // 只查询 name 字段
                .where("code").equalsTo("000001")
                .single(String.class); // 指定返回结果为String


        // GoodsVo 属性有 maxPrice, supplierCount 和函数的别名对应
        GoodsVo goodsVo = jdbcTools.select(Goods.class)
                .function("max", "price").alias("maxPrice")
                .function("count", true, "supplierCode").alias("supplierCount")
                .single(GoodsVo.class);
    }


    private Goods getGoods() {
        return Goods.builder()
                .code("000001")
                .barCode("6920152971535")
                .name("哇哈哈矿泉水")
                .specification("550ml")
                .unit("瓶")
                .inventory(200)
                .price(2D)
                .purchaserCode("33018002")
                .category("100041")
                .categoryName("矿泉水")
                .supplierCode("09689")
                .build();
    }

}
