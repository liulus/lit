package com.github.lit.support.data.jdbc;

import com.github.lit.support.data.SQL;
import com.github.lit.support.data.SQLUtils;
import com.github.lit.support.data.domain.*;
import com.github.lit.support.util.ClassUtils;
import com.github.lit.support.util.bean.BeanUtils;
import com.github.lit.support.util.lamabda.SerializedFunction;
import com.github.lit.support.util.lamabda.SerializedLambdaUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-10 14:28
 */
@Slf4j
@NoArgsConstructor
public class JdbcRepositoryImpl implements JdbcRepository {

    private static final String PARAM = "param";

    private static final String PARAM_EQ = " = :param";

    private static final String PARAM_IN = " in (:param)";

    @Setter
    private String dbName;

    @Getter
    @Setter
    private NamedParameterJdbcOperations jdbcOperations;


    private String getDbName() {
        if (StringUtils.isEmpty(dbName)) {
            dbName = jdbcOperations.getJdbcOperations()
                    .execute((ConnectionCallback<String>) con -> con.getMetaData().getDatabaseProductName());
        }
        return dbName;
    }

    public JdbcRepositoryImpl(DataSource dataSource) {
        this.jdbcOperations = new NamedParameterJdbcTemplate(dataSource);
    }

    public JdbcRepositoryImpl(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public <E> int insert(E entity) {
        Objects.requireNonNull(entity, "insert with entity can not be null");
        Class<?> entityClass = entity.getClass();
        TableMetaDate metaDate = TableMetaDate.forClass(entityClass);
        String sql = SQLUtils.insertSQL(entity, SQL.Type.JDBC).toString();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource sqlParameterSource = getSqlParameterSource(entity);
        logSqlAndParams(sql, sqlParameterSource);
        int insert = jdbcOperations.update(sql, sqlParameterSource, keyHolder);
        PropertyDescriptor keyPs = BeanUtils.getPropertyDescriptor(entityClass, metaDate.getKeyProperty());
        // set key property
        if (keyPs != null) {
            ReflectionUtils.invokeMethod(keyPs.getWriteMethod(), entity,
                    Objects.requireNonNull(keyHolder.getKey()).longValue());
        }
        return insert;
    }

    @Override
    public <E> int batchInsert(Collection<E> eList) {
        if (CollectionUtils.isEmpty(eList)) {
            return 0;
        }
        E entity = eList.iterator().next();
        SQL sql = SQLUtils.insertSQL(entity, SQL.Type.JDBC);

        SqlParameterSource[] parameterSources = eList.stream()
                .map(this::getSqlParameterSource).toArray(SqlParameterSource[]::new);
        int[] updateResult = jdbcOperations.batchUpdate(sql.toString(), parameterSources);

        int row = 0;
        for (int res : updateResult) {
            row += res;
        }
        return row;
    }


    @Override
    public <E> int update(E entity) {
        SQL sql = SQLUtils.updateSQL(entity, false, SQL.Type.JDBC);
        return jdbcOperations.update(sql.toString(), getSqlParameterSource(entity));
    }

    @Override
    public <E> int updateSelective(E entity) {
        String sql = SQLUtils.updateSQL(entity, true, SQL.Type.JDBC).toString();
        SqlParameterSource sqlParameterSource = getSqlParameterSource(entity);
        logSqlAndParams(sql, sqlParameterSource);
        return jdbcOperations.update(sql, sqlParameterSource);
    }

    @Override
    public <E> int delete(E entity) {
        Assert.notNull(entity, "deleteById with entity can not be null");
        TableMetaDate metaDate = TableMetaDate.forClass(entity.getClass());
        PropertyDescriptor keyPs = BeanUtils.getPropertyDescriptor(entity.getClass(), metaDate.getKeyProperty());
        Assert.notNull(keyPs, "can not find key property from " + entity.getClass().getName());
        Object keyValue = ReflectionUtils.invokeMethod(keyPs.getReadMethod(), entity);
        Assert.notNull(keyValue, "key value can not be null");

        String sql = SQLUtils.deleteSQL(entity.getClass(), SQL.Type.JDBC).toString();
        SqlParameterSource sqlParam = getSqlParameterSource(Collections.singletonMap(metaDate.getKeyProperty(), keyValue));
        logSqlAndParams(sql, sqlParam);
        return jdbcOperations.update(sql, sqlParam);
    }

    @Override
    public <E> int deleteById(Class<E> eClass, Long id) {
        Assert.notNull(id, "id can not be null");
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);
        String sql = SQLUtils.deleteSQL(eClass, SQL.Type.JDBC).toString();
        SqlParameterSource sqlParam = getSqlParameterSource(Collections.singletonMap(metaDate.getKeyProperty(), id));
        logSqlAndParams(sql, sqlParam);
        return jdbcOperations.update(sql, sqlParam);
    }

    @Override
    public <E> int deleteByIds(Class<E> eClass, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);
        String sql = SQL.init().DELETE_FROM(metaDate.getTableName())
                .WHERE(metaDate.getKeyColumn() + PARAM_IN)
                .toString();
        SqlParameterSource sqlParam = getSqlParameterSource(Collections.singletonMap(PARAM, ids));
        logSqlAndParams(sql, sqlParam);
        return jdbcOperations.update(sql, sqlParam);
    }

    @Override
    public <E> E selectById(Class<E> eClass, Long id) {
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);

        SQL sql = SQL.init().SELECT(metaDate.getBaseColumns())
                .FROM(metaDate.getTableName())
                .WHERE(metaDate.getKeyColumn() + PARAM_EQ);

        return selectForObject(sql, Collections.singletonMap(PARAM, id), eClass);
    }

    @Override
    public <E> List<E> selectByIds(Class<E> eClass, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);

        SQL sql = SQL.init().SELECT(metaDate.getBaseColumns())
                .FROM(metaDate.getTableName())
                .WHERE(metaDate.getKeyColumn() + PARAM_IN);

        return selectForList(sql, Collections.singletonMap(PARAM, ids), eClass);
    }

    @Override
    public <E> List<E> selectAll(Class<E> eClass) {
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);
        String sql = SQL.init().SELECT(metaDate.getBaseColumns())
                .FROM(metaDate.getTableName())
                .toString();
        logSqlAndParams(sql, null);
        return jdbcOperations.query(sql, Collections.emptyMap(), AnnotationRowMapper.newInstance(eClass));
    }

    @Override
    public <E, R> E selectByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        Class<E> eClass = SerializedLambdaUtils.getLambdaClass(serializedFunction);
        SQL sql = getSelectByPropertySql(serializedFunction, eClass);
        return selectForObject(sql, Collections.singletonMap(PARAM, value), eClass);
    }

    @Override
    public <E, R> List<E> selectListByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        Class<E> eClass = SerializedLambdaUtils.getLambdaClass(serializedFunction);
        String sql = getSelectByPropertySql(serializedFunction, eClass).toString();
        SqlParameterSource sqlParams = getSqlParameterSource(Collections.singletonMap(PARAM, value));
        logSqlAndParams(sql, sqlParams);
        return jdbcOperations.query(sql, sqlParams, AnnotationRowMapper.newInstance(eClass));
    }

    private <E, R> SQL getSelectByPropertySql(SerializedFunction<E, R> serializedFunction, Class<E> eClass) {
        String property = SerializedLambdaUtils.getProperty(serializedFunction);

        TableMetaDate metaDate = TableMetaDate.forClass(eClass);
        String column = metaDate.getFieldColumnMap().get(property);

        return SQL.init().SELECT(metaDate.getBaseColumns())
                .FROM(metaDate.getTableName())
                .WHERE(column + PARAM_EQ);
    }

    @Override
    public <E, C> List<E> selectList(Class<E> eClass, C condition) {
        return selectListWithOrder(eClass, condition, null);
    }

    @Override
    public <E, C> List<E> selectListWithOrder(Class<E> eClass, C condition, Sort sort) {
        String sql = SQLUtils.selectSQL(eClass, condition, sort, SQL.Type.JDBC).toString();
        SqlParameterSource sqlParams = getSqlParameterSource(condition);
        logSqlAndParams(sql, sqlParams);
        return jdbcOperations.query(sql, sqlParams, AnnotationRowMapper.newInstance(eClass));
    }

    @Override
    public <E, C extends Pageable> Page<E> selectPageList(Class<E> eClass, C condition) {

        SQL sql = SQLUtils.selectSQL(eClass, condition, condition.getSort(), SQL.Type.JDBC);
        return selectForPageList(sql, condition, eClass);
    }

    @Override
    public <E> E selectForObject(SQL sql, Object args, Class<E> requiredType) {
        List<E> rs = selectForList(sql, args, requiredType);
        if (CollectionUtils.isEmpty(rs)) {
            return null;
        }
        if (rs.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, rs.size());
        }
        return rs.iterator().next();
    }

    @Override
    public <E> List<E> selectForList(SQL sql, Object args, Class<E> requiredType) {
        if (args == null) {
            args = Collections.emptyMap();
        }
        String sqlStr = sql.toString();
        SqlParameterSource parameterSource = getSqlParameterSource(args);
        logSqlAndParams(sqlStr, parameterSource);
        if (ClassUtils.isSimpleValueType(requiredType)) {
            return jdbcOperations.queryForList(sqlStr, parameterSource, requiredType);
        }
        return jdbcOperations.query(sqlStr, parameterSource, AnnotationRowMapper.newInstance(requiredType));
    }

    @Override
    public <E> Page<E> selectForPageList(SQL sql, Pageable args, Class<E> requiredType) {
        Integer count = 0;
        SqlParameterSource sqlParameterSource = getSqlParameterSource(args);
        if (args.isCount()) {
            String countSql = sql.countSql();
            logSqlAndParams(countSql, sqlParameterSource);
            count = jdbcOperations.queryForObject(countSql, sqlParameterSource, int.class);
            if (count == null || count <= 0) {
                return Page.emptyPage();
            }
        }
        String pageSql = SQLUtils.getPageSql(getDbName(), sql.toString(), args.getPageSize(), args.getPageNum());
        logSqlAndParams(pageSql, sqlParameterSource);
        List<E> rs = jdbcOperations.query(pageSql, sqlParameterSource, AnnotationRowMapper.newInstance(requiredType));

        PageInfo pageInfo = new PageInfo(args.getPageSize(), args.getPageNum(), count);
        return new Page<>(rs, pageInfo);
    }

    @Override
    public <E> int count(Class<E> eClass) {
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);
        SQL sql = SQL.init().SELECT("count(*)")
                .FROM(metaDate.getTableName());
        return selectForObject(sql, Collections.emptyMap(), int.class);
    }

    @Override
    public <E, R> int countByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        Class<E> eClass = SerializedLambdaUtils.getLambdaClass(serializedFunction);
        String property = SerializedLambdaUtils.getProperty(serializedFunction);

        TableMetaDate metaDate = TableMetaDate.forClass(eClass);

        SQL sql = SQL.init().SELECT("count(*)")
                .FROM(metaDate.getTableName())
                .WHERE(metaDate.getColumn(property) + PARAM_EQ);

        return selectForObject(sql, Collections.singletonMap(PARAM, value), int.class);
    }


    private void logSqlAndParams(String sql, SqlParameterSource params) {
        if (params == null || params.getParameterNames() == null) {
            log.info("\n sql: {} \n params: ", sql);
            return;
        }
        StringBuilder paramLog = new StringBuilder();
        for (String parameterName : params.getParameterNames()) {
            Object value = params.getValue(parameterName);
            if (!StringUtils.isEmpty(value)) {
                paramLog.append(parameterName).append("=").append(value).append(", ");
            }
        }
        log.info("\n sql: {} \n params: {}", sql, paramLog);
    }

    private <E> SqlParameterSource getSqlParameterSource(E params) {
        if (params == null) {
            return new EmptySqlParameterSource();
        }
        Map<String, Object> paramMap = params instanceof Map ? (Map) params : BeanUtils.beanToMap(params);
        return new MapSqlParameterSource(paramMap);
    }

}
