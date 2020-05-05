package com.github.lit.starter.jdbc;

import com.github.lit.support.data.jdbc.JdbcRepository;
import com.github.lit.support.data.jdbc.JdbcRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-22 19:27
 */
@Configuration
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class JdbcSupportAutoConfiguration {

    @Value("${lit.support.jdbc.database:}")
    private String database;

    @Value("${lit.support.jdbc.dataSource:}")
    private String dataSourceName;

    @Value("${lit.support.jdbc.template:}")
    private String templateName;

    @ConditionalOnMissingBean(JdbcRepository.class)
    public JdbcRepository jdbcRepository(ApplicationContext context) {
        JdbcRepositoryImpl jdbcRepository = new JdbcRepositoryImpl();
        if (StringUtils.hasText(database)) {
            jdbcRepository.setDbName(database.toUpperCase());
        }

        Map<String, NamedParameterJdbcOperations> jdbcOperationsBeans
                = context.getBeansOfType(NamedParameterJdbcOperations.class);
        if (jdbcOperationsBeans == null || jdbcOperationsBeans.isEmpty()) {
            Map<String, DataSource> dataSourceBeans = context.getBeansOfType(DataSource.class);
            if (dataSourceBeans == null || dataSourceBeans.isEmpty()) {
                throw new IllegalArgumentException("no DataSource or NamedParameterJdbcOperations bean find for jdbcRepository");
            }
            if (StringUtils.isEmpty(dataSourceName)) {
                DataSource dataSource = dataSourceBeans.values().iterator().next();
                jdbcRepository.setJdbcOperations(new NamedParameterJdbcTemplate(dataSource));
                return jdbcRepository;
            }
            DataSource dataSource = dataSourceBeans.get(dataSourceName);
            if (dataSource == null) {
                throw new IllegalArgumentException("no DataSource bean named: " + dataSourceName);
            }
            jdbcRepository.setJdbcOperations(new NamedParameterJdbcTemplate(dataSource));
            return jdbcRepository;
        }

        if (StringUtils.isEmpty(templateName)) {
            jdbcRepository.setJdbcOperations(jdbcOperationsBeans.values().iterator().next());
            return jdbcRepository;
        }
        NamedParameterJdbcOperations jdbcOperations = jdbcOperationsBeans.get(templateName);
        if (jdbcOperations == null) {
            throw new IllegalArgumentException("no NamedParameterJdbcOperations bean named: " + templateName);
        }
        jdbcRepository.setJdbcOperations(jdbcOperations);

        return jdbcRepository;
    }

}
