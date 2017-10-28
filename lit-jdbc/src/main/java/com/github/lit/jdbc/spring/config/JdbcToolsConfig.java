package com.github.lit.jdbc.spring.config;

import com.github.lit.jdbc.JdbcTools;
import com.github.lit.jdbc.spring.JdbcTemplateToolsImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcOperations;

import javax.sql.DataSource;
import java.util.Map;

/**
 * User : liulu
 * Date : 2017/6/4 20:44
 * version $Id: JdbcToolsConfig.java, v 0.1 Exp $
 */
@Configuration
public class JdbcToolsConfig {


    @Bean
    public JdbcTools jdbcTools(ApplicationContext context, Environment environment) {

        JdbcTemplateToolsImpl templateTools = new JdbcTemplateToolsImpl();
        templateTools.setDbName(environment.getProperty("lit.jdbc.dbName"));

        Map<String, JdbcOperations> jdbcOperationsBeans = context.getBeansOfType(JdbcOperations.class);
        if (jdbcOperationsBeans == null || jdbcOperationsBeans.isEmpty()) {
            Map<String, DataSource> dataSourceBeans = context.getBeansOfType(DataSource.class);
            if (dataSourceBeans == null || dataSourceBeans.isEmpty()) {
                throw new RuntimeException("to enable JdbcTools, need config DataSource or JdbcOperations bean...");
            }
            String dataSourceBeanName = environment.getProperty("lit.jdbc.dataSource");
            if (dataSourceBeanName == null || dataSourceBeanName.isEmpty()) {
                templateTools.setDataSource(dataSourceBeans.values().iterator().next());
                return templateTools;
            } else {
                DataSource dataSource = dataSourceBeans.get(dataSourceBeanName);
                if (dataSource == null) {
                    throw new RuntimeException("no DataSource bean named: " + dataSourceBeanName);
                }
                templateTools.setDataSource(dataSource);
                return templateTools;
            }
        }

        String templateBeanName = environment.getProperty("lit.jdbc.template");
        if (templateBeanName == null || templateBeanName.isEmpty()) {
            templateTools.setJdbcOperations(jdbcOperationsBeans.values().iterator().next());
        } else {
            JdbcOperations jdbcOperations = jdbcOperationsBeans.get(templateBeanName);
            if (jdbcOperations == null) {
                throw new RuntimeException("no JdbcOperations bean named: " + templateBeanName);
            }
            templateTools.setJdbcOperations(jdbcOperations);
        }

        return templateTools;
    }
}
