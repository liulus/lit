# lit-tools

lit-jdbc 是一个简单易用的jdbc工具,

可以在service或其他需要的地方注入 JdbcTools 来完成对数据库的操作

## 如何使用?

目前 还没有发布到maven中央库, 可以 install 到本地库, 通过 maven 引用

        <dependency>
            <groupId>com.github.lit</groupId>
            <artifactId>lit-jdbc</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
    
然后在配置类中添加 `@EnableLitJdbc` 注解即可, 前提是已经配置了数据源 或 JdbcOperations 的bean

        @Configuration
        @EnableLitJdbc
        public class SpringConfig {
            // 其他配置
        }

如果是使用 `xml` 配置的话, 配置 `JdbcToolsConfig` bean 

        <bean class="com.github.lit.jdbc.spring.config.JdbcToolsConfig">
        </bean> 
    
配置完成后, 就可以在 service 中注入 `JdbcTools` 对实体进行操作, 基本不在需要 dao 了

实体与数据库表的对应关系:
> 类名 对应表名, 默认驼峰转下划线<br>
> 主键 默认类名 + Id 首字母小写的驼峰命名<br>
> 属性名 对应表字段名, 默认驼峰转下划线

如果不想使用默认转换规则, 可以使用 `@Table`, `@Column`, `@Id`, `@Transient` 注解

下面以商品实体为例, 介绍 JdbcTools 的具体用法, 数据库是 MySql 

        @Table(name = "lit_goods")
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
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
            private String purchaserCode; // 采购员
            private String supplierCode; // 供应商
            private String name; // 品名
            private Double price; // 售价
            private Integer inventory; // 库存
            private Date gmtCreate; // 创建时间
        }


### Insert

        public void testInsert() {
            Goods goods = new Goods();
            goods.setCode("80145124");
            // ...
            Long id = (Long) jdbcTools.insert(goods);
            log.info("插入的实体ID为: {}", id);
        }
        
    
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

还支持下面这种操作方式:

        @Test
        public void testInsert() {
            Long id = (Long) jdbcTools.createInsert(Goods.class)
                            .into("code", "80145412")
                            .into("name", "农夫山泉")
                            .into("price", 2D)
                            .execute();
            log.info("插入的实体ID为: {}", id);
        }

### Update

        @Test
        public void testUpdate() {
            // update 操作是根据实体的 id 来更新记录, 所以要保证实体中的 Id一定不为空
            
    
            Goods goods = new Goods();
            goods.setGoodsId(12301L);
            goods.setName("康师傅绿茶");
            
    
            //方式一 实体中值为 null 的属性 不会 更新到数据库中
            jdbcTools.update(goods);
            
    
            //方式二 实体中值为 null 的属性 会 更新到数据库中，
            jdbcTools.update(goods, false);
        }
        
或者

        @Test
        public void testUpdate2() {
    
            // 会将 code 为 80145412 的商品的 name 和 price 更新, 返回受影响记录的条数
            
            int rows = jdbcTools.createUpdate(Goods.class)
                            .set("name", "统一红茶")
                            .set("price", 3.0D)
                            .where("code", "80145412")
                            .execute();
        }

### Delete
 
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
                    .where("code", "80145412")
                    .execute();
        }
   
        
### Select 查询

#### 直接根据 Id 查出对应的实体
    
        @Test
        public void testSelect1() {
            Goods goods = jdbcTools.get(Goods.class, 1203L);
        }

#### 指定查询条件 查询列表, 分页列表, count, 单条记录

        @Test
        public void testSelect2() {
            Select<Goods> select = jdbcTools.createSelect(Goods.class)
                    .where("code", "8014512") // 添加条件 code='8014512'
                    .and("price", Logic.GTEQ, 20.98D) // 添加条件 and price >= 20.98
                    .or("inventory", Logic.LT, 120); // 添加条件 or inventory < 120
                    
            // 返回 count
            int count = select.count();
            
    
            // 返回 单个 实体对象, 如果查询结果有多条, 会抛出异常, 查询结果为空, 返回 null
            Goods goods = select.single();
            
    
            // 返回实体列表
            List<Goods> list = select.list();
            
    
            // 返回分页查询结果列表, 可以强转 为 PageList, 获取分页信息
            List<Goods> pageList1 = select.page(1, 20).list();
            
    
            // 返回分页查询结果列表, 但是 不会查询 count, 只查询分页结果
            List<Goods> pageList2 = select.page(1, 20, false).list();
        }
        
#### 分页查询

分页查询没有单独的方法, 而是集中在了 list()方法中,<br>
若需要分页查询, 只需要把通过 jdbcTools 获取的 `Select` 对象设置分页参数( page() 方法), 再通过 list() 获取分页后的结果集,<br>
`page()` 有 3 个重载方法, 第一个参数是 当前页码 , 第二个参数是 每页条数 , 第三个参数是 是否查询 count<br>
list() 返回的 List 对象, 如果是分页查询, 可以将此 List 强转成 `PageList` (一个继承 ArrayList 的 List, 包含分页的相关信息)
    
#### 指定查询黑名单和白名单

        @Test
        public void testSelect3() {
            // 指定白名单, 将只查询 白名单里的字段
            List<Goods> goodsList1 = jdbcTools.createSelect(Goods.class)
                    .include("code", "name") // 只查询 code 和 name
                    .where("name", Logic.LIKE, "%娃哈哈%") //  条件 name like ? 参数 %娃哈哈%
                    .andWithBracket("price", Logic.LTEQ, 38.7D) // 添加带有括号的 and条件   and ( price <= ? 参数 38.7
                    .or("supplierCode", "00776") // 条件 supplier_code=? 参数 00776
                    .end() // 结束刚才 and 条件添加的括号  )
                    .list(); // 查询列表   
                    
                    
            // 指定黑名单, 除了黑名单里的字段, 其他全部查询
            List<Goods> goodsList2 = jdbcTools.createSelect(Goods.class)
                    .exclude("code", "name") // 查询将排除 code 和 name
                    .list(); // 查询列表
        }

#### 添加函数

注: 如果添加了函数, 就只会把 include (白名单) 的字段 和添加的函数 放到 select 后

        @Test
        public void testSelect4() {
    
            // 1. 指定函数名, 默认全部字段, 结果会添加  count(*)
            Integer count = jdbcTools.createSelect(Goods.class)
                    .addFunc("count")
                    .single(Integer.class); // 指定函数返回类型
    
            // 2. 指定函数名和字段, 结果会添加  max(price)
            jdbcTools.createSelect(Goods.class)
                    .addFunc("max", "price")
                    .single(Double.class); // 指定返回类型
    
            // 3. 指定函数名和字段, 第二个参数是 isDistinct, 结果会添加 count( distinct supplier_code )
            jdbcTools.createSelect(Goods.class)
                    .addFunc("count", true, "supplierCode")
                    .single(Integer.class);
        }
        
#### 别名

上面只添加了一个函数, 返回的是简单类型, 
如果 select 后面有多个函数, 且想把返回记录绑定到对象的属性上, 可以将函数的别名设置为对象的属性名

        @Test
        public void testSelect5() {
    
            // 为函数指定别名 和 查询结果的 返回类型
            // max(price) 的查询结果将会绑定到 GoodsVo 的 maxPrice 属性上
            // count(inventory) 的查询结果将会绑定到 GoodsVo 的 inventoryCount 属性上
            jdbcTools.createSelect(Goods.class)
                    .addFunc("max", "price")
                    .addFunc("count", "inventory")
                    .alias("maxPrice", "inventoryCount")
                    .single(GoodsVo.class); // 指定返回类型
    
            // 另一种写法
            jdbcTools.createSelect(Goods.class)
                    .addFunc("max", "price").alias("maxPrice")
                    .addFunc("count", "inventory").alias("inventoryCount")
                    .single(GoodsVo.class);
        }

#### 排序

        @Test
        public void testSelect6() {
    
            // 注: asc 和 desc 方法 可以放多个字段
            List<Goods> list = jdbcTools.createSelect(Goods.class)
                    .where("name", Logic.LIKE, "%娃哈哈%")
                    .asc("code") // 按 code 升序排列
                    .desc("gmtCreate") // 按创建时间降序排列
                    .list();
        }
        
#### 分组查询 group by

        @Test
        public void testSelect7() {
    
            // 按供应商代码 supplierCode 查询 最大价格和最大库存
            jdbcTools.createSelect(Goods.class)
                    .include("supplierCode")
                    .addFunc("max", "price")
                    .addFunc("max", "inventory")
                    .alias("maxPrice", "maxInventory")
                    .where("price", Logic.GTEQ, 19.98)
                    .and("inventory", Logic.GTEQ, 10)
                    .groupBy("supplierCode")
                    // 添加 having 条件
                    .having("maxPrice", Logic.GTEQ, 198)
                    .and("maxInventory", Logic.GTEQ, 100)
                    .list(GoodsVo.class);
        }
        
#### join

以供应商表为关联表示例

        @Table(name = "lit_supplier")
        @Data
        @Builder
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

写法如下:

        @Test
        public void testSelect8() {
    
            jdbcTools.createSelect(Goods.class)
                    // join 语句, 和 on 一起使用 设置关联条件
                    .join(Supplier.class)
                    .on(Supplier.class, "code", Logic.EQ, Goods.class, "supplierCode")
    
                    // 添加 供应商表中的字段
                    .addField(Supplier.class, "name")
                    .addField(Supplier.class, "address")
    
                    // 设置别名, 与 GoodsVo 中属性名一致,
                    .alias("supplierName", "supplierAddr")
                    .where("price", Logic.GTEQ, 19.98)
                    .list(GoodsVo.class);
        }

这样 join 条件 会用 on 连接

    SELECT lit_supplier.name AS supplierName, lit_supplier.address AS supplierAddr, lit_goods.unit, lit_goods.goods_id, lit_goods.category, lit_goods.price, lit_goods.gmt_create, lit_goods.purchaser_code, lit_goods.inventory, lit_goods.name, lit_goods.specification, lit_goods.bar_code, lit_goods.supplier_code, lit_goods.code 
    FROM lit_goods JOIN lit_supplier ON lit_supplier.code = lit_goods.supplier_code WHERE price >= ?


还可以使用简单 join 将关联条件放到 where 中

        @Test
        public void testSelect9() {
    
            jdbcTools.createSelect(Goods.class)
                    // 使用简单join  需要和 joinCondition 一起使用设置关联条件
                    .simpleJoin(Supplier.class)
    
                    // 添加 供应商表中的字段
                    .addField(Supplier.class, "name")
                    .addField(Supplier.class, "address")
    
                    // 设置别名, 与 GoodsVo 中属性名一致,
                    .alias("supplierName", "supplierAddr")
                    .joinCondition(Supplier.class, "code", Logic.EQ, Goods.class, "supplierCode")
                    .and("price", Logic.GTEQ, 19.98)
                    .list(GoodsVo.class);
        }

结果如下:

    SELECT lit_supplier.name AS supplierName, lit_supplier.address AS supplierAddr, lit_goods.unit, lit_goods.goods_id, lit_goods.category, lit_goods.price, lit_goods.gmt_create, lit_goods.purchaser_code, lit_goods.inventory, lit_goods.name, lit_goods.specification, lit_goods.bar_code, lit_goods.supplier_code, lit_goods.code 
    FROM lit_goods, lit_supplier WHERE lit_supplier.code = lit_goods.supplier_code AND price >= ?


以上内容 若有不正确的地方, 欢迎指出

