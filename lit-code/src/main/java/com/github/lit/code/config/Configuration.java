package com.github.lit.code.config;

import com.github.lit.code.context.Table;
import com.github.lit.code.context.Task;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User : liulu
 * Date : 2018/2/6 16:26
 * version $Id: Configuration.java, v 0.1 Exp $
 */
@Getter
@Setter
@ToString
public class Configuration {

    /**
     * 默认的转换映射 map
     */
    private static final Map<String, String> DEFAULT_CONVERT_MAP = new ConcurrentHashMap<>();

    /**
     * 常量Map
     */
    private Map<String, String> constantMap = new HashMap<>();

    /**
     * jdbcType 和 javaType 转换映射 Map
     */
    private Map<String, String> converterMap = new HashMap<>();

    /**
     * 任务Map
     */
    private Map<String, Task> taskMap = new HashMap<>();

    /**
     * jdbc连接
     */
    private JdbcConfig jdbcConfig;

    /**
     * 表配置
     */
    private Table table;


    static {
        DEFAULT_CONVERT_MAP.put(JDBCType.REAL.getName(), Float.class.getName());
        DEFAULT_CONVERT_MAP.put(JDBCType.DECIMAL.getName(), BigDecimal.class.getName());
        DEFAULT_CONVERT_MAP.put(JDBCType.NUMERIC.getName(), BigDecimal.class.getName());
        DEFAULT_CONVERT_MAP.put(JDBCType.CHAR.getName(), String.class.getName());
        DEFAULT_CONVERT_MAP.put(JDBCType.LONGVARCHAR.getName(), String.class.getName());
        DEFAULT_CONVERT_MAP.put(JDBCType.CLOB.getName(), String.class.getName());
        DEFAULT_CONVERT_MAP.put(JDBCType.BINARY.getName(), Byte.class.getName() + "[]");
        DEFAULT_CONVERT_MAP.put(JDBCType.VARBINARY.getName(), Byte.class.getName() + "[]");
        DEFAULT_CONVERT_MAP.put(JDBCType.LONGVARBINARY.getName(), Byte.class.getName() + "[]");
        DEFAULT_CONVERT_MAP.put(JDBCType.BLOB.getName(), Byte.class.getName() + "[]");
        DEFAULT_CONVERT_MAP.put(JDBCType.DATE.getName(), Date.class.getName());
        DEFAULT_CONVERT_MAP.put(JDBCType.TIME.getName(), Date.class.getName());
        DEFAULT_CONVERT_MAP.put(JDBCType.TIMESTAMP.getName(), Date.class.getName());

        // mySql
        DEFAULT_CONVERT_MAP.put("TINYTEXT", String.class.getName());
        DEFAULT_CONVERT_MAP.put("TEXT", String.class.getName());
        DEFAULT_CONVERT_MAP.put("LONGTEXT", String.class.getName());
    }


    public void addConstant(String key, String value) {
        constantMap.put(key, value);
    }

    public String getConstant(String key) {
        return constantMap.get(key);
    }

    public String getConstant(String key, String defaultValue) {
        String value = constantMap.get(key);
        if (value == null || value.length() == 0) {
            value = defaultValue;
        }
        return value;
    }

    public Boolean getBooleanConstant(String key) {
        String value = constantMap.get(key);
        if (value == null || value.length() == 0) {
            return false;
        }
        return Boolean.valueOf(value);
    }

    public Boolean getBooleanConstant(String key, Boolean defaultValue) {
        String value = constantMap.get(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return Boolean.valueOf(value);
    }

    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        taskMap.put(task.getName(), task);
    }

    public Task getTask(String key) {
        return taskMap.get(key);
    }

    public void addConverter(String key, String value) {
        converterMap.put(key, value);
    }

    public String getConverter(String key) {
        String value = converterMap.get(key);
        if (value == null || value.isEmpty()) {
            value = DEFAULT_CONVERT_MAP.get(key);
        }
        return value;
    }

}
