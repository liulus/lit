package com.github.lit.code.parser;

import com.github.lit.code.config.Configuration;
import com.github.lit.code.context.ConfigConst;
import com.oracle.javafx.jmx.json.JSONDocument;

import java.util.Map;

/**
 * User : liulu
 * Date : 2018/2/7 15:13
 * version $Id: ConstantParser.java, v 0.1 Exp $
 */
public class ConstantParser implements ConfigParser {

    @Override
    public String getConfigKey() {
        return ConfigConst.CONSTANT;
    }

    @Override
    public void parser(Configuration configuration, JSONDocument jsonDocument) {
        if (!jsonDocument.isObject()) {
            return;
        }
        for (Map.Entry<String, Object> entry : jsonDocument.object().entrySet()) {
            String value = String.valueOf(entry.getValue());
            if (value == null || value.length() == 0 || "null".equals(value)) {
                continue;
            }
            configuration.addConstant(entry.getKey(), value);
        }
    }
}
