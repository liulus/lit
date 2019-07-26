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
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-10 14:28
 */
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

    public JdbcRepositoryImpl(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public <E> int insert(E entity) {
        Objects.requireNonNull(entity, "insert with entity can not be null");
        Class<?> entityClass = entity.getClass();
        TableMetaDate metaDate = TableMetaDate.forClass(entityClass);
        SQL sql = SQLUtils.insertSQL(entity, SQL.Type.JDBC);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int insert = jdbcOperations.update(sql.toString(), getSqlParameterSource(entity), keyHolder);
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
        SQL sql = SQLUtils.updateSQL(entity, true, SQL.Type.JDBC);
        return jdbcOperations.update(sql.toString(), getSqlParameterSource(entity));
    }

    @Override
    public <E> int delete(E entity) {
        Assert.notNull(entity, "deleteById with entity can not be null");
        TableMetaDate metaDate = TableMetaDate.forClass(entity.getClass());
        PropertyDescriptor keyPs = BeanUtils.getPropertyDescriptor(entity.getClass(), metaDate.getKeyProperty());
        Assert.notNull(keyPs, "can not find key property from " + entity.getClass().getName());
        Object keyValue = ReflectionUtils.invokeMethod(keyPs.getReadMethod(), entity);
        Assert.notNull(keyValue, "key value can not be null");

        SQL sql = SQLUtils.deleteSQL(entity.getClass(), SQL.Type.JDBC);

        return jdbcOperations.update(sql.toString(), Collections.singletonMap(metaDate.getKeyProperty(), keyValue));
    }

    @Override
    public <E> int deleteById(Class<E> eClass, Long id) {
        Assert.notNull(id, "id can not be null");
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);
        SQL sql = SQLUtils.deleteSQL(eClass, SQL.Type.JDBC);
        return jdbcOperations.update(sql.toString(), Collections.singletonMap(metaDate.getKeyProperty(), id));
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
        return jdbcOperations.update(sql, Collections.singletonMap(PARAM, ids));
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
        SQL sql = getSelectByPropertySql(serializedFunction, eClass);
        return jdbcOperations
                .query(sql.toString(), Collections.singletonMap(PARAM, value), AnnotationRowMapper.newInstance(eClass));
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
        SQL sql = SQLUtils.selectSQL(eClass, condition, sort, SQL.Type.JDBC);
        return jdbcOperations
                .query(sql.toString(), getSqlParameterSource(condition), AnnotationRowMapper.newInstance(eClass));
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
        SqlParameterSource parameterSource = getSqlParameterSource(args);

        if (ClassUtils.isSimpleValueType(requiredType)) {
            return jdbcOperations.queryForList(sql.toString(), parameterSource, requiredType);
        }
        return jdbcOperations.query(sql.toString(), parameterSource, AnnotationRowMapper.newInstance(requiredType));
    }

    @Override
    public <E> Page<E> selectForPageList(SQL sql, Pageable args, Class<E> requiredType) {
        Integer count = 0;
        if (args.isCount()) {
            String countSql = sql.countSql();
            count = jdbcOperations.queryForObject(countSql, getSqlParameterSource(args), int.class);
            if (count == null || count <= 0) {
                return Page.emptyPage();
            }
        }
        String pageSql = SQLUtils.getPageSql(getDbName(), sql.toString(), args.getPageSize(), args.getPageNum());
        List<E> rsList = jdbcOperations
                .query(pageSql, getSqlParameterSource(args), AnnotationRowMapper.newInstance(requiredType));

        PageInfo pageInfo = new PageInfo(args.getPageSize(), args.getPageNum(), count);
        return new Page<>(rsList, pageInfo);
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


    private <E> SqlParameterSource getSqlParameterSource(E e) {
        if (e == null) {
            return new EmptySqlParameterSource();
        }
        if (e instanceof Map) {
            //noinspection unchecked
            return new MapSqlParameterSource((Map<String, ?>) e);
        }
        return new BeanPropertySqlParameterSource(e);
    }

}
