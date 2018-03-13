package com.github.lit.code.parser;

import com.github.lit.code.config.Configuration;
import com.github.lit.code.context.ConfigConst;
import com.github.lit.code.context.GenerationException;
import com.oracle.javafx.jmx.json.JSONDocument;

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
    public void parser(Configuration configuration, JSONDocument jsonDocument) {
        if (jsonDocument.isArray()) {
            throw new GenerationException("列转换 配置项不能是数组!");
        }

        for (Map.Entry<String, Object> entry : jsonDocument.object().entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            configuration.addConverter(entry.getKey().toUpperCase(), entry.getValue().toString());
        }
    }
}
