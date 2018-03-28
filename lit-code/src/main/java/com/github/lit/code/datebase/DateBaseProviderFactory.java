package com.github.lit.code.datebase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User : liulu
 * Date : 2018/2/10 16:06
 * version $Id: DateBaseProviderFactory.java, v 0.1 Exp $
 */
public class DateBaseProviderFactory {

    private static final Map<String, DataBaseProvider> PROVIDER_MAP = new ConcurrentHashMap<>();

    static {
        registerProvider(new MySqlProvider());
    }

    public static void registerProvider(DataBaseProvider dataBaseProvider) {
        PROVIDER_MAP.put(dataBaseProvider.getDbName().toUpperCase(), dataBaseProvider);
    }


    public static DataBaseProvider getDataBaseProvider(String dbName) {
        return PROVIDER_MAP.get(dbName.toUpperCase());
    }



}
