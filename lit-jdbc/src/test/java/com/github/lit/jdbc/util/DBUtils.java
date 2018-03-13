package com.github.lit.jdbc.util;

import com.github.lit.commons.util.PropertyUtils;
import com.github.lit.jdbc.AbstractJdbcTools;
import com.github.lit.jdbc.JdbcTools;
import com.github.lit.jdbc.spring.JdbcTemplateToolsImpl;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * User : liulu
 * Date : 2017-1-9 21:14
 * version $Id: DBUtils.java, v 0.1 Exp $
 */
public class DBUtils {

    private static final String CONFIG = "config";

    private static final String DB = "mysql.";

    private static JdbcTools jdbcTools;

    private static JdbcOperations jdbcTemplate;

    private static DataSource dataSource;

    public static JdbcTools getJdbcTools() {
        if (jdbcTools == null) {
//            jdbcTools = new JdbcTemplateToolsImpl(getJdbcOperations());
            jdbcTools = new JdbcTemplateToolsImpl(new JdbcTemplate());
            ((AbstractJdbcTools) jdbcTools).setDbName("MYSQL");
        }
        return jdbcTools;
    }

    public static JdbcOperations getJdbcOperations() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(getDataSource());
        }
        return jdbcTemplate;
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            ComboPooledDataSource source = new ComboPooledDataSource();
            source.setJdbcUrl(PropertyUtils.getProperty(CONFIG, DB + "url"));
            try {
                source.setDriverClass(PropertyUtils.getProperty(CONFIG, DB + "driver"));
            } catch (PropertyVetoException e) {
                // do nothing
            }
            source.setUser(PropertyUtils.getProperty(CONFIG, DB + "user"));
            source.setPassword(PropertyUtils.getProperty(CONFIG, DB + "password"));
            source.setMinPoolSize(Integer.valueOf(PropertyUtils.getProperty(CONFIG, "pool.minPoolSize")));
            source.setMaxPoolSize(Integer.valueOf(PropertyUtils.getProperty(CONFIG, "pool.maxPoolSize")));
            source.setAutoCommitOnClose(false);
            source.setCheckoutTimeout(Integer.valueOf(PropertyUtils.getProperty(CONFIG, "pool.checkoutTimeout")));
            source.setAcquireRetryAttempts(2);
            dataSource = source;
        }
        return dataSource;
    }
}
