package com.github.lit.code.parser;

import com.github.lit.code.config.Configuration;
import com.github.lit.code.config.JdbcConfig;
import com.github.lit.code.context.ConfigConst;
import com.github.lit.code.context.GenerationException;
import com.github.lit.code.util.BeanUtils;
import com.oracle.javafx.jmx.json.JSONDocument;

/**
 * User : liulu
 * Date : 2018/2/6 15:10
 * version $Id: JdbcParser.java, v 0.1 Exp $
 */
public class JdbcParser implements ConfigParser {

    @Override
    public String getConfigKey() {
        return ConfigConst.JDBC;
    }

    @Override
    public void parser(Configuration configuration, JSONDocument jsonDocument) {
        if (jsonDocument.isArray()) {
            throw new GenerationException("table 配置项不能是数组!");
        }
        JdbcConfig jdbcConfig = BeanUtils.mapToBean(jsonDocument.object(), JdbcConfig.class);
        if (jdbcConfig.getDbName() != null && !jdbcConfig.getDbName().isEmpty()) {
            jdbcConfig.setDbName(jdbcConfig.getDbName().toUpperCase());
        }
        configuration.setJdbcConfig(jdbcConfig);
    }
}
