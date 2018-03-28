-- MYSQL 建表语句
CREATE TABLE test_goods (
  goods_Id       BIGINT                  NOT NULL AUTO_INCREMENT PRIMARY KEY
  COMMENT '商品Id',
  code           VARCHAR(32) DEFAULT ''  NOT NULL
  COMMENT '商品编码',
  bar_code       VARCHAR(32) DEFAULT ''  NOT NULL
  COMMENT '商品条形码',
  name           VARCHAR(256) DEFAULT '' NOT NULL
  COMMENT '商品名称',
  specification  VARCHAR(128) DEFAULT '' NOT NULL
  COMMENT '规格',
  unit           VARCHAR(32) DEFAULT ''  NOT NULL
  COMMENT '计量单位',
  category       VARCHAR(32) DEFAULT ''  NOT NULL
  COMMENT '类别',
  category_name  VARCHAR(62) DEFAULT ''  NOT NULL
  COMMENT '类别名称',
  price          DECIMAL(12, 2) COMMENT '价格',
  purchaser_code VARCHAR(32) COMMENT '采购员',
  supplier_code  VARCHAR(32) COMMENT '供应商',
  inventory      BIGINT COMMENT '库存',
  gmt_Create     TIMESTAMP                        DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  INDEX idx_code (code)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  DEFAULT CHARSET = utf8
  COMMENT = '商品表';

CREATE TABLE test_supplier (
  supplier_Id BIGINT NOT NULL AUTO_INCREMENT
  COMMENT '供应商Id',
  code        VARCHAR(32) COMMENT '供应商编码',
  name        VARCHAR(256) COMMENT '供应商名称',
  address     VARCHAR(512) COMMENT '供应商地址',
  contact     VARCHAR(32) COMMENT '联系人',
  telephone   VARCHAR(32) COMMENT '联系电话',
  mobile      VARCHAR(32) COMMENT '手机号',
  PRIMARY KEY (supplier_Id)
)
  DEFAULT CHARSET = utf8
  COMMENT = '供应商';