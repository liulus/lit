package com.lit.code.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lit.code.context.ConfigConst;
import com.lit.code.context.GenerationException;
import com.lit.code.parser.*;
import com.lit.support.util.ClassUtils;
import com.lit.support.util.TokenUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
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
            throw new GenerationException(String.format("未能找到配置文件 %s, 请确认该文件放在 classpath 路径下", configFile));
        }

        Configuration configuration = new Configuration();
        JsonElement rootElement = new JsonParser().parse(new InputStreamReader(inputStream));
        if (rootElement.isJsonArray()) {
            throw new GenerationException("配置文件不能是数组");
        }

        // 提前解析出所有的常量
        JsonObject constant = rootElement.getAsJsonObject().getAsJsonObject().getAsJsonObject(ConfigConst.CONSTANT);
        for (Map.Entry<String, JsonElement> entry : constant.entrySet()) {
            configuration.addConstant(entry.getKey(), entry.getValue().getAsString());
        }

        // 替换 json 配置中的 ${} 占位符
        String rootConfigJson = TokenUtils.parseToken(configuration.getConstantMap(), rootElement.toString());
        JsonObject rootObject = new JsonParser().parse(rootConfigJson).getAsJsonObject();

        LOGGER.info("开始解析配置文件: " + configFile);
        for (ConfigParser configParser : CONFIG_PARSERS) {
            JsonElement jsonElement = rootObject.get(configParser.getConfigKey());
            if (jsonElement != null) {
                configParser.parser(configuration, jsonElement);
            }
        }
        LOGGER.info("完成配置文件: " + configFile + " 的解析!");
        return configuration;
    }


    public static void registerParser(ConfigParser configParser) {
        CONFIG_PARSERS.add(configParser);
    }
}
