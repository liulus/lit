package com.github.lit.support.configure;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import com.github.lit.support.data.jdbc.EnableJdbcSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-14 21:04
 */
@Configuration
@EnableJdbcSupport
public class SpringTestConfigure {



    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public DB initDb() throws ManagedProcessException {
        DB db = DB.newEmbeddedDB(3306);
        db.start();
        return db;
    }

    @Bean
    public DataSource dataSource() {

        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306");
        dataSource.setUsername("root");
        dataSource.setPassword("");

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

//    @Bean
//    public NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
//        return new NamedParameterJdbcTemplate(dataSource);
//    }

//    @Bean
//    public JdbcRepository jdbcRepository(NamedParameterJdbcOperations jdbcOperations) {
//        return new JdbcRepositoryImpl(jdbcOperations);
//    }

}
