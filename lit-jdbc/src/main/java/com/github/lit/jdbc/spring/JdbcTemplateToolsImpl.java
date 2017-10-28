package com.github.lit.jdbc.spring;

import com.github.lit.jdbc.AbstractJdbcTools;
import com.github.lit.jdbc.StatementExecutor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * User : liulu
 * Date : 2017/6/4 11:42
 * version $Id: JdbcTemplateToolsImpl.java, v 0.1 Exp $
 */
@NoArgsConstructor
public class JdbcTemplateToolsImpl extends AbstractJdbcTools {

    @Setter
    private JdbcOperations jdbcOperations;

    public JdbcTemplateToolsImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    protected StatementExecutor getDefaultExecutor() {
        if (jdbcOperations == null) {
            if (dataSource == null) {
                throw new RuntimeException("JdbcOperations is null, can not init..");
            }
            jdbcOperations = new JdbcTemplate(dataSource);
        }

        return new JdbcTemplateExecutor(jdbcOperations);
    }

    @Override
    protected String getDefaultDbName() {
        return jdbcOperations.execute(new ConnectionCallback<String>() {
                @Override
                public String doInConnection(Connection con) throws SQLException, DataAccessException {
                    return con.getMetaData().getDatabaseProductName().toUpperCase();
                }
            });
    }
}
