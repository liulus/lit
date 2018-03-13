package com.github.lit.code.config;

import com.github.lit.code.context.ConfigConst;
import com.github.lit.code.context.GenerationException;
import com.github.lit.code.parser.*;
import com.github.lit.code.util.ClassUtils;
import com.github.lit.code.util.TokenUtils;
import com.oracle.javafx.jmx.json.JSONDocument;
import com.oracle.javafx.jmx.json.JSONFactory;
import com.oracle.javafx.jmx.json.JSONReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * User : liulu
 * Date : 2018/2/7 14:49
 * version $Id: ConfigurationBuilder.java, v 0.1 Exp $
 */
public class ConfigurationBuilder {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationBuilder.class.getName());

    private static final List<ConfigParser> CONFIG_PARSERS = new ArrayList<>();

    static {
//        CONFIG_PARSERS.add(new ConstantParser());
        CONFIG_PARSERS.add(new JdbcParser());
        CONFIG_PARSERS.add(new ConverterParser());
        CONFIG_PARSERS.add(new TableParser());
        CONFIG_PARSERS.add(new TasksParser());
    }

    public static Configuration build(String configFile) {
        if (configFile.startsWith("/")) {
            configFile = configFile.substring(1);
        }
        InputStream inputStream = ClassUtils.getDefaultClassLoader().getResourceAsStream(configFile);
        if (inputStream == null) {
            throw new GenerationException(String.format("未能找到配置文件 %s, 请确认该文件放在 classpath 路径下",configFile));
        }

        Configuration configuration = new Configuration();

        JSONReader jsonReader = JSONFactory.instance().makeReader(new InputStreamReader(inputStream));
        JSONDocument rootDocument = jsonReader.build();

        // 提前解析出所有的常量
        Map<String, Object> constMap = rootDocument.getMap(ConfigConst.CONSTANT);
        for (Map.Entry<String, Object> entry : constMap.entrySet()) {
            configuration.addConstant(entry.getKey(), String.valueOf(entry.getValue()));
        }

        // 替换 json 配置中的 ${} 占位符
        String rootConfigJson = TokenUtils.parseToken(constMap, rootDocument.toJSON());
        JSONDocument rootConfigDoc = JSONFactory.instance().makeReader(new StringReader(rootConfigJson)).build();

        LOGGER.info("开始解析配置文件: " + configFile);
        for (ConfigParser configParser : CONFIG_PARSERS) {
            JSONDocument jsonDocument = rootConfigDoc.get(configParser.getConfigKey());
            if (jsonDocument != null) {
                configParser.parser(configuration, jsonDocument);
            }
        }
        LOGGER.info("完成配置文件: " + configFile + " 的解析!");
        return configuration;
    }


    public static void registerParser(ConfigParser configParser) {
        CONFIG_PARSERS.add(configParser);
    }
}
