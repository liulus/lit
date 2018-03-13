package com.github.lit.code.parser;

import com.github.lit.code.config.Configuration;
import com.oracle.javafx.jmx.json.JSONDocument;

/**
 * User : liulu
 * Date : 2018/2/6 16:31
 * version $Id: ConfigParser.java, v 0.1 Exp $
 */
public interface ConfigParser {

    String getConfigKey();

    void parser(Configuration configuration, JSONDocument jsonDocument);
}
