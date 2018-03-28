-- MYSQL 建表语句
CREATE TABLE test_goods (
    goods_Id bigint NOT NULL AUTO_INCREMENT COMMENT '商品Id',
    code VARCHAR(32) NOT NULL COMMENT '商品编码',
    bar_code VARCHAR(32) COMMENT '商品条形码',
    name VARCHAR(256) COMMENT '商品名称',
    specification VARCHAR(128) COMMENT '规格',
    unit VARCHAR(32) COMMENT '计量单位',
    category VARCHAR(32) COMMENT '类别',
    price DECIMAL(12,2) COMMENT '价格',
    purchaser_code VARCHAR(32) COMMENT '采购员',
    supplier_code VARCHAR(32)  COMMENT '供应商',
    inventory bigint COMMENT '库存',
    gmt_Create TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (goods_Id),
    INDEX idx_code (code)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='商品表';

CREATE TABLE lit_supplier (
    supplier_Id bigint NOT NULL AUTO_INCREMENT COMMENT '供应商Id',
    code VARCHAR(32)  COMMENT '供应商编码',
    name VARCHAR(256) COMMENT '供应商名称',
    address VARCHAR(512) COMMENT '供应商地址',
    contact VARCHAR (32) COMMENT '联系人',
    telephone VARCHAR (32)  COMMENT '联系电话',
    mobile VARCHAR (32) COMMENT '手机号',
    PRIMARY KEY (supplier_Id)
)DEFAULT CHARSET=utf8 COMMENT='供应商';

CREATE TABLE lit_dictionary
(
    dict_id INT unsigned PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '字典Id',
    dict_key VARCHAR(128) NOT NULL DEFAULT '' COMMENT '字典key',
    dict_value VARCHAR(128) NOT NULL DEFAULT '' COMMENT '字典值',
    order_num SMALLINT unsigned COMMENT '顺序号',
    dict_level TINYINT unsigned COMMENT '字典层级',
    memo VARCHAR(512) DEFAULT '' COMMENT '备注',
    is_system TINYINT COMMENT '是否系统字典数据',
    parent_id INT unsigned COMMENT '上级字典Id',
    sys_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='字典表';

CREATE INDEX fk_pid_did ON lit_dictionary (parent_id);




-- ORACLE 建表语句
CREATE TABLE lit_goods(
    goods_Id INT NOT NULL,
    code VARCHAR2(32) NOT NULL,
    bar_code VARCHAR2(32)
    name VARCHAR2(256),
    specification VARCHAR2(128),
    unit VARCHAR2(32),
    category VARCHAR2(32),
    price DECIMAL(12,2),
    purchaser_code VARCHAR2(32),
    supplier_code VARCHAR2(32),
    inventory INTEGER,
    gmt_Create TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (goods_Id),
);
CREATE UNIQUE INDEX "lit_goods_code_uindex" ON lit_goods (code);
COMMENT ON TABLE lit_goods IS '商品表';
COMMENT ON COLUMN lit_goods.goods_Id IS '商品Id';
COMMENT ON COLUMN lit_goods.code IS '商品编码';
COMMENT ON COLUMN lit_goods.bar_code IS '商品条形码';
COMMENT ON COLUMN lit_goods.name IS '商品名称';
COMMENT ON COLUMN lit_goods.specification IS '规格';
COMMENT ON COLUMN lit_goods.unit IS '计量单位';
COMMENT ON COLUMN lit_goods.category IS '类别';
COMMENT ON COLUMN lit_goods.price IS '价格';
COMMENT ON COLUMN lit_goods.purchaser_code IS '采购员';
COMMENT ON COLUMN lit_goods.supplier_code IS '供应商';
COMMENT ON COLUMN lit_goods.inventory IS '库存';
COMMENT ON COLUMN lit_goods.gmt_Create IS '创建时间';

CREATE TABLE lit_supplier (
    supplier_Id INT NOT NULL AUTO_INCREMENT COMMENT '供应商Id',
    code VARCHAR(32)  COMMENT '供应商编码',
    name VARCHAR(256) COMMENT '供应商名称',
    address VARCHAR(512) COMMENT '供应商地址',
    contact VARCHAR (32) COMMENT '联系人',
    telephone VARCHAR (32)  COMMENT '联系电话',
    mobile VARCHAR (32) COMMENT '手机号',
    PRIMARY KEY (supplier_Id)
);
CREATE UNIQUE INDEX "lit_supplier_code_uindex" ON lit_supplier (code);
COMMENT ON COLUMN lit_supplier.supplier_Id IS '供应商Id';
COMMENT ON COLUMN lit_supplier.code IS '供应商编码';
COMMENT ON COLUMN lit_supplier.name IS '供应商名称';
COMMENT ON COLUMN lit_supplier.address IS '供应商名称';
COMMENT ON COLUMN lit_supplier.contact IS '联系人';
COMMENT ON COLUMN lit_supplier.telephone IS '联系电话';
COMMENT ON COLUMN lit_supplier.mobile IS '手机号';

