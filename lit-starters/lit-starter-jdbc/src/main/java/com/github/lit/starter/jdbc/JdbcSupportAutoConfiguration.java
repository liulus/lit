package com.github.lit.starter.jdbc;

import com.github.lit.support.data.jdbc.EnableJdbcSupport;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-22 19:27
 */
@Configuration
@EnableJdbcSupport
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class JdbcSupportAutoConfiguration {


}
