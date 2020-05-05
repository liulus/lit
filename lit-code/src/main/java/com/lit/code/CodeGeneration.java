package com.lit.code;

import com.lit.code.config.Configuration;
import com.lit.code.config.ConfigurationBuilder;
import com.lit.code.context.GenerationException;
import com.lit.code.datebase.DataBaseProvider;
import com.lit.code.datebase.DateBaseProviderFactory;
import com.lit.code.executor.GenerationExecutor;
import com.lit.code.parser.ConfigParser;

import java.util.logging.Logger;

/**
 * User : liulu
 * Date : 2018/2/6 15:03
 * version $Id: CodeGeneration.java, v 0.1 Exp $
 */
public class CodeGeneration {

    private static final Logger LOGGER = Logger.getLogger(CodeGeneration.class.getName());

    public static void run() {
        run("code.json");
    }

    public static void run(String configFile) {
        if (configFile == null || configFile.length() == 0) {
            throw new GenerationException("配置文件名称不能为空");
        }
        Configuration configuration = ConfigurationBuilder.build(configFile);
        GenerationExecutor.execute(configuration);
    }

    public static void registerParser(ConfigParser configParser) {
        ConfigurationBuilder.registerParser(configParser);
    }

    public static void registerProvider(DataBaseProvider dataBaseProvider) {
        DateBaseProviderFactory.registerProvider(dataBaseProvider);
    }

}
