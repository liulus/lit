# lit-jdbc

lit-jdbc 是一个简单易用的jdbc工具, 

借鉴 [selfly@dexcoder-assistant](https://github.com/selfly/dexcoder-assistant)

可以在service或其他需要的地方注入 JdbcTools 来完成对数据库的操作

## 如何使用?

目前 已经发布到maven中央库, 通过 maven 引用

```xml
    <dependency>
        <groupId>com.github.lit</groupId>
        <artifactId>lit-jdbc</artifactId>
        <version>2.0</version>
    </dependency>
```

然后在配置类中添加 `@EnableLitJdbc` 注解即可, 前提是已经配置了`DataSource` 或 `JdbcOperations` 的bean

```java
    @Configuration
    @EnableLitJdbc
    public class SpringConfig {
        // 其他配置
    }
```

如果是使用 `xml` 配置的话, 配置 `JdbcToolsConfig` bean 

```xml
    <bean class="com.github.lit.jdbc.spring.config.JdbcToolsConfig"/>
```
    
配置完成后, 就可以在 service 或其他需要的地方注入 `JdbcTools` 对实体进行操作, 基本不在需要 dao 了

```java
    @Service
    public class TestService {
        
        @Resource
        private JdbcTools jdbcTools;
        
        // service中的其他方法
    }
```

实体与数据库表的默认对应关系:
> 类名 对应表名, 默认首字母大写驼峰命名   
> 主键 默认类名 + Id 首字母小写的驼峰命名    
> 属性名 对应表字段名, 默认首字母小写驼峰命名

如果不想使用默认转换规则, 可以使用 `@Table`, `@Column`, `@Id`, `@Transient` 注解

下面以商品实体为例, 介绍 JdbcTools 的具体用法, 数据库是 MySql 

```java
    @Table(name = "lit_goods")
    public class Goods implements Serializable {
    
        private static final long serialVersionUID = -7915472099544797458L;
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long goodsId; // 商品编码
        private String code; // 编码
        private String barCode; // 条形码
        private String specification; // 规格
        private String unit; // 单位
        private String category; // 分类
        private String categoryName; // 分类名称
        private String purchaserCode; // 采购员
        private String supplierCode; // 供应商
        private String name; // 品名
        private Double price; // 售价
        private Integer inventory; // 库存
        private Date gmtCreate; // 创建时间
    }
```

### Insert

```
    @Test
    public void testInsert1() {
        Goods goods = new Goods();
        goods.setCode("000001");
        // ...省略其他set方法
        

        Long id = jdbcTools.insert(goods);
        log.info("插入的实体ID为: {}", id);
    }
```
    
insert 操作会返回实际插入数据库后的主键值, 自增长和 SEQUENCE 会返回 Long 型.

#### 主键生成策略

* 数据库自增长

      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long goodsId; // 商品编码
      
* 使用数据库序列, 如果没有指定 `sequenceName`, 默认是 `seq_` + 表名
      
      @GeneratedValue(strategy = GenerationType.SEQUENCE, sequenceName = "seq_goods")
      private Long goodsId; // 商品编码
      
* 自定义主键生成, 实现 `KeyGenerator` 接口的类都可以作为主键生成类
      
      @GeneratedValue(generator = UUIDGenerator.class)
      private Long goodsId; // 商品编码

还支持下面这种操作方式  
set方法第一个参数是属性名, 第二个参数是属性的值, 执行 sql 值会以 ? 的形代替  
若值想直接拼接在sql语句中, 可以使用 natively().set("code", "'000002'"), 这样sql中会直接拼接 '000002'

```
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
```

### Update

```
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
```
        
或者自定义更新条件

```
    @Test
    public void testUpdate2() {
    
        // 会将 code 为 80145412 的商品的 name 和 price 更新, 返回受影响记录的条数
        int rows = jdbcTools.createUpdate(Goods.class)
                .set("name", "哇哈哈矿泉水-大瓶")
                .set("price", 4.0D)
                .where("code").equalsTo("000001")
                .execute();
    }
```

详细的 where 的写法在下面 select 的介绍

### Delete
 
```
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
```
   
        
### Select 查询

#### 直接根据 Id 查出对应的实体

```
    @Test
    public void testSelect1() {
        // 直接根据 Id 查出对应的实体
        Goods goods = jdbcTools.get(Goods.class, 1203L);
        log.info(goods.toString());
    }
```

#### 构建 Select 对象

```
    @Test
    public void testSelect() {
        // 直接使用 select 方法获取 select 对象
        Select<Goods> select = jdbcTools.select(Goods.class);
    }
```

* 指定 select 的字段

    ```
        @Test
        public void testSelect() {
        
            // 默认查询实体属性中对应的所有字段
            Select<Goods> select = jdbcTools.select(Goods.class);
            
            // 使用 include 方法指定查询的字段, 将只查询 code, name, price 对应的字段
            Select<Goods> include = jdbcTools.select(Goods.class)
                    .include("code", "name", "price");
                    
            // 使用 exclude 方法指定需要排除查询的字段, 将查询除了 code, name 外所有实体属性中对应的字段
            Select<Goods> exclude = jdbcTools.select(Goods.class)
                    .exclude("code", "name");
        }
    ```
* 指定查询函数

    ```
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
    ```

指定查询函数后将不再默认添加实体属性对应的字段放到 select 中, 可以使用 include 方法添加字段

* 指定字段别名

    ```
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
    ```
    
* 多表关联查询指定关联条件和添加关联表字段

以供应商表为示例关联

```java
    @Table(name = "test_supplier")
    public class Supplier implements Serializable {
    
        private static final long serialVersionUID = 548793140920612818L;
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long supplierId;
    
        private String code;
        private String name;
        private String address;
        private String contact;
        private String telephone;
        private String mobile;
    }
```

多表关联查询指定关联条件和添加关联表字段的使用方法如下

```
        @Test
        public void testSelect5() {
            // 关联查询 goods 表和 supplier 表
            // on 方法指定关联条件 -> goods.supplier_code = supplier.code
            // equalsTo 指定关联条件的逻辑
            // additionalField 方法添加查询关联表的字段
            Select<Goods> join = jdbcTools.select(Goods.class)
                    .join(Supplier.class)
                    .additionalField(Supplier.class, "name", "address")
                    .alias("supplierName", "supplierAddress")
                    .on(Goods.class, "supplierCode").equalsTo(Supplier.class, "code");
        }        
```

* 指定 where 条件

where 中的逻辑( >, <=, like) 等以方法的形式代替, 可用的方法有:  
   like, notLike, isNull, isNotNull, in, notIn  
   equalsTo(=), notEqualsTo(!=), lessThan(<), lessThanOrEqual(<=), graterThan(>), graterThanOrEqual(>=)

```
    @Test
    public void testSelect6() {
    
        // 指定条件 code < ? and price > ? and code in (?, ?, ?), ? 参数即为条件逻辑方法中的值
        jdbcTools.select(Goods.class)
                .where("code").lessThan(1123L)
                .and("price").graterThan(548D)
                .and("code").in(1027L, 1078L, 1094L);
    }
```

如果要在 where 语句中添加 () 来提高优先级, 可以使用 `bracket` 方法添加左(, 并使用`end`方法添加右 )

```
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
```

* order by 排序

    ```
        @Test
        public void testSelect8() {
            // 注: asc 和 desc 方法 可以放多个字段
            jdbcTools.select(Goods.class)
                    .where("code").like("0000%")
                    .asc("code") // 按 code 升序排列
                    .desc("gmtCreate"); // 按创建时间降序排列
    
        }
    ```

* group by 分组和指定 having 条件
    
    ```
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
    ```
    
    having 条件的用法参见 where 条件

#### 获取查询结果

Select 对象可以获取 count, 单个对象, 列表, 分页列表  
获取分页列表只需调用page() 方法后再调用list(), 返回的就是带分页信息的列表了，

```
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
        PageInfo pageInfo = goodsPage.getPageInfo();


        // 返回分页查询结果列表, 但是 不会查询 count, 只查询分页结果
        List<Goods> pageList2 = select.page(1, 20, false).list();
    }
```


如果需要返回实体类型以外的类型, 可以在获取结果的方法里指定返回类型

```
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
```

以上内容 若有不正确的地方, 欢迎指出 QQ: 874607990

