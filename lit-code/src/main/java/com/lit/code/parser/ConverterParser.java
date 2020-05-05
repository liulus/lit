package com.lit.code.parser;

import com.lit.code.config.Configuration;
import com.lit.code.context.ConfigConst;
import com.lit.code.context.GenerationException;
import com.google.gson.JsonElement;

import java.util.Map;

/**
 * User : liulu
 * Date : 2018/2/9 15:12
 * version $Id: ConverterParser.java, v 0.1 Exp $
 */
public class ConverterParser implements ConfigParser {

    @Override
    public String getConfigKey() {
        return ConfigConst.CONVERTER;
    }

    @Override
    public void parser(Configuration configuration, JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            throw new GenerationException("列转换 配置项不能是数组!");
        }
        for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            configuration.addConverter(entry.getKey().toUpperCase(), entry.getValue().getAsString());
        }
    }
}
