package com.lit.starter.jdbc;

import com.lit.support.data.jdbc.JdbcExecutor;
import com.lit.support.data.jdbc.JdbcExecutorImpl;
import com.lit.support.util.SpringContextUtils;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-22 19:27
 */
@Configuration
@EnableConfigurationProperties
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class JdbcSupportAutoConfiguration {

    @Resource
    private JdbcSupportProperties jdbcSupportProperties;

    @Bean
    @ConditionalOnMissingBean(SpringContextUtils.class)
    public SpringContextUtils springContextUtils () {
        return new SpringContextUtils();
    }

    @Bean
    @ConditionalOnMissingBean(NamedParameterJdbcOperations.class)
    public NamedParameterJdbcOperations litNamedParameterJdbcOperations(Map<String, DataSource> dataSourceMap) {
        if (CollectionUtils.isEmpty(dataSourceMap)) {
            throw new IllegalArgumentException("no dataSource fund, please config a DataSource");
        }
        String dataSourceId = jdbcSupportProperties.getDataSource();
        if (StringUtils.hasText(dataSourceId)) {
            DataSource dataSource = Optional.of(dataSourceId)
                    .map(dataSourceMap::get)
                    .orElseThrow(() -> new IllegalArgumentException("no dataSource bean with name " + dataSourceId));
            return new NamedParameterJdbcTemplate(dataSource);
        }
        if (dataSourceMap.size() == 1) {
            return new NamedParameterJdbcTemplate(dataSourceMap.values().iterator().next());
        }
        throw new IllegalArgumentException("to many dataSource bean fund, please config one with lit.support.jdbc.data-source: [you dataSource bean id]");
    }


    @Bean
    @ConditionalOnMissingBean(JdbcExecutor.class)
    public JdbcExecutor jdbcRepository(Map<String, DataSource> dataSourceMap,
                                       List<NamedParameterJdbcOperations> namedParameterJdbcOperations) {
        if (CollectionUtils.isEmpty(namedParameterJdbcOperations)) {
            throw new IllegalArgumentException("no NamedParameterJdbcOperations fund, please config at least one");
        }
        String dataSourceId = jdbcSupportProperties.getDataSource();
        if (StringUtils.hasText(dataSourceId)) {
            DataSource dataSource = Optional.of(dataSourceId)
                    .map(dataSourceMap::get)
                    .orElseThrow(() -> new IllegalArgumentException("no dataSource bean with name " + dataSourceId));
            return buildJdbcRepository(dataSource, namedParameterJdbcOperations);
        }
        if (dataSourceMap.size() == 1) {
            return buildJdbcRepository(dataSourceMap.values().iterator().next(), namedParameterJdbcOperations);
        }
        throw new IllegalArgumentException("to many dataSource bean fund, please config one with lit.support.jdbc.data-source: [you dataSource bean id]");
    }


    private JdbcExecutor buildJdbcRepository(DataSource dataSource, List<NamedParameterJdbcOperations> jdbcOperations) {
        for (NamedParameterJdbcOperations namedParameterJdbcOperations : jdbcOperations) {
            JdbcTemplate jdbcTemplate = (JdbcTemplate) namedParameterJdbcOperations.getJdbcOperations();
            if (Objects.equals(jdbcTemplate.getDataSource(), dataSource)) {
                return new JdbcExecutorImpl(namedParameterJdbcOperations);
            }
        }
        throw new IllegalArgumentException("no JdbcTemplate find with dataSource " + dataSource.toString());
    }


}
