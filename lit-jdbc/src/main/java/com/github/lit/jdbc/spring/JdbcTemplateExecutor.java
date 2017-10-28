package com.github.lit.jdbc.spring;

import com.github.lit.commons.util.ClassUtils;
import com.github.lit.jdbc.AbstractStatementExecutor;
import com.github.lit.jdbc.model.StatementContext;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * User : liulu
 * Date : 2017/6/4 11:47
 * version $Id: JdbcTemplateExecutor.java, v 0.1 Exp $
 */
@NoArgsConstructor
@Slf4j
public class JdbcTemplateExecutor extends AbstractStatementExecutor {

    private static final Class<?>[] SINGLE_CLASS_ARRAY = new Class<?>[]{Number.class, String.class};


    @Setter
    private JdbcOperations jdbcTemplate;

    public JdbcTemplateExecutor(JdbcOperations jdbcOperations) {
        this.jdbcTemplate = jdbcOperations;
    }


    @Override
    public Object insert(final StatementContext context) {

        final String insert = context.getSql();
        final Object[] args = context.getParams().toArray();

        if (context.isGenerateKeyByDb()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            log.info("\nsql : {}  args : {}", insert, Arrays.toString(args));
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(insert, new String[]{context.getPkColumn()});
                    ArgumentPreparedStatementSetter apss = new ArgumentPreparedStatementSetter(args);
                    apss.setValues(ps);
                    return ps;
                }
            }, keyHolder);
            return keyHolder.getKey().longValue();
        }
        executeUpdate(insert, args);
        return null;
    }

    @Override
    public int delete(StatementContext context) {
        return executeUpdate(context.getSql(), context.getParams().toArray());
    }

    @Override
    public int update(StatementContext context) {
        return executeUpdate(context.getSql(), context.getParams().toArray());
    }

    private int executeUpdate(String sql, Object... args) {
        log.info("\nsql : {}  args : {}", sql, Arrays.toString(args));
        return jdbcTemplate.update(sql, args);
    }

    @Override
    public Object selectSingle(StatementContext context) {
        List results = (List) selectList(context);
        if (results == null || results.size() == 0) {
            return null;
        }
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        }
        return results.iterator().next();
    }

    @Override
    public Object selectList(StatementContext context) {
        String sql = context.getSql();
        Object[] args = context.getParams().toArray();
        log.info("\nsql : {}  args : {}", sql, Arrays.toString(args));
        if (ClassUtils.isPrimitiveOrWrapper(context.getRequireType())) {
            return jdbcTemplate.queryForList(sql, args, context.getRequireType());
        }

        for (Class<?> clazz : SINGLE_CLASS_ARRAY) {
            if (clazz.isAssignableFrom(context.getRequireType())) {
                return jdbcTemplate.queryForList(sql, args, context.getRequireType());
            }
        }
        AnnotationRowMapper<?> rowMapper = AnnotationRowMapper.newInstance(context.getRequireType());
        rowMapper.addMappedField(context.getEntityClass());
        return jdbcTemplate.query(sql, args, rowMapper);
    }

}
