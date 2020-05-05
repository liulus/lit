package com.lit.code.parser;

import com.lit.code.config.Configuration;
import com.lit.code.config.JdbcConfig;
import com.lit.code.context.ConfigConst;
import com.lit.code.context.GenerationException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

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
    public void parser(Configuration configuration, JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            throw new GenerationException("table 配置项不能是数组!");
        }

        JdbcConfig jdbcConfig = new Gson().fromJson(jsonElement.toString(), JdbcConfig.class);
        if (jdbcConfig.getDbName() != null && !jdbcConfig.getDbName().isEmpty()) {
            jdbcConfig.setDbName(jdbcConfig.getDbName().toUpperCase());
        }
        configuration.setJdbcConfig(jdbcConfig);
    }
}
