CREATE DATABASE IF NOT EXISTS testdb ;
USE testdb;

DROP TABLE IF EXISTS sign_product;
CREATE TABLE sign_product (
    id         BIGINT PRIMARY KEY      NOT NULL AUTO_INCREMENT,
    code       VARCHAR(32) DEFAULT ''  NOT NULL,
    full_name  VARCHAR(128) DEFAULT '' NOT NULL,
    inventory  INT DEFAULT 0           NOT NULL,
    gmt_create TIMESTAMP                        DEFAULT current_timestamp,
    gmt_update TIMESTAMP                        DEFAULT current_timestamp
    ON UPDATE current_timestamp
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;


INSERT INTO sign_product  (code, full_name, inventory) VALUES ('893341', '远见产品3号', 12);
INSERT INTO sign_product  (code, full_name, inventory) VALUES ('213324', '这是测试产品', 543);
INSERT INTO sign_product  (code, full_name, inventory) VALUES ('123873', '正式环境产品53', 76);
INSERT INTO sign_product  (code, full_name, inventory) VALUES ('678567', '招商银行限量', 9765);

