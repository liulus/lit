package com.lit.starter.jdbc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liulu
 * @version 1.0
 * created_at 2020/5/11
 */
@Data
@ConfigurationProperties(prefix = "lit.support.jdbc")
public class JdbcSupportProperties {

    private String database;
    private String dataSource;
//    private String jdbcTemplate;

}
