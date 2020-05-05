package com.lit.code.parser;

import com.lit.code.config.Configuration;
import com.google.gson.JsonElement;

/**
 * User : liulu
 * Date : 2018/2/6 16:31
 * version $Id: ConfigParser.java, v 0.1 Exp $
 */
public interface ConfigParser {

    String getConfigKey();

    void parser(Configuration configuration, JsonElement jsonElement);
}
