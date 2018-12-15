package com.github.lit.support.jdbc;

import com.github.lit.support.common.Database;
import com.github.lit.support.common.Logic;
import com.github.lit.support.common.TableMataDate;
import com.github.lit.support.common.annotation.Condition;
import com.github.lit.support.common.page.PageList;
import com.github.lit.support.common.page.PageParam;
import com.github.lit.support.jdbc.dialect.Dialect;
import com.github.lit.support.util.SerializedFunction;
import com.github.lit.support.util.SerializedLambdaUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-10 14:28
 */
@NoArgsConstructor
public class JdbcRepositoryImpl implements JdbcRepository {

    @Setter
    private Database database;

    @Getter
    @Setter
    private NamedParameterJdbcOperations jdbcOperations;

    public JdbcRepositoryImpl(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public <E> int insert(E entity) {
        Assert.notNull(entity, "insert with entity can not be null");
        Class<?> entityClass = entity.getClass();
        TableMataDate mataDate = TableMataDate.forClass(entityClass);
        Map<String, String> fieldColumnMap = mataDate.getFieldColumnMap();
        SQL sql = SQL.init().INSERT_INTO(mataDate.getTableName());
        for (Map.Entry<String, String> entry : fieldColumnMap.entrySet()) {
            // 忽略主键
            PropertyDescriptor ps = BeanUtils.getPropertyDescriptor(entityClass, entry.getKey());
            if (Objects.equals(entry.getKey(), mataDate.getKeyProperty())
                    || ps == null || ps.getReadMethod() == null) {
                continue;
            }
            Object value = ReflectionUtils.invokeMethod(ps.getReadMethod(), entity);
            if (!StringUtils.isEmpty(value)) {
                sql.VALUES(entry.getValue(), getNamedParam(entry.getKey()));
            }
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int insert = jdbcOperations.update(sql.toString(), new BeanPropertySqlParameterSource(entity), keyHolder);
        PropertyDescriptor keyPs = BeanUtils.getPropertyDescriptor(entityClass, mataDate.getKeyProperty());
        ReflectionUtils.invokeMethod(keyPs.getWriteMethod(), entity, keyHolder.getKey().longValue());
        return insert;
    }

    @Override
    public <E> int update(E entity) {
        Assert.notNull(entity, "update with entity can not be null");
        Class<?> entityClass = entity.getClass();
        TableMataDate mataDate = TableMataDate.forClass(entityClass);
        Map<String, String> fieldColumnMap = mataDate.getFieldColumnMap();

        SQL sql = SQL.init().UPDATE(mataDate.getTableName());
        for (Map.Entry<String, String> entry : fieldColumnMap.entrySet()) {
            // 忽略主键
            if (Objects.equals(entry.getKey(), mataDate.getKeyProperty())) {
                continue;
            }
            sql.SET(getEqCondition(entry.getValue(), entry.getKey()));
        }
        sql.WHERE(getEqCondition(mataDate.getKeyColumn(), mataDate.getKeyProperty()));

        return jdbcOperations.update(sql.toString(), new BeanPropertySqlParameterSource(entity));
    }

    @Override
    public <E> int updateSelective(E entity) {
        Assert.notNull(entity, "update with entity can not be null");
        Class<?> entityClass = entity.getClass();
        TableMataDate mataDate = TableMataDate.forClass(entityClass);
        Map<String, String> fieldColumnMap = mataDate.getFieldColumnMap();

        SQL sql = SQL.init().UPDATE(mataDate.getTableName());
        for (Map.Entry<String, String> entry : fieldColumnMap.entrySet()) {
            // 忽略主键
            PropertyDescriptor ps = BeanUtils.getPropertyDescriptor(entityClass, entry.getKey());
            if (Objects.equals(entry.getKey(), mataDate.getKeyProperty())
                    || ps == null || ps.getReadMethod() == null) {
                continue;
            }
            Object value = ReflectionUtils.invokeMethod(ps.getReadMethod(), entity);
            if (value != null) {
                sql.SET(getEqCondition(entry.getValue(), entry.getKey()));
            }
        }
        sql.WHERE(getEqCondition(mataDate.getKeyColumn(), mataDate.getKeyProperty()));

        return jdbcOperations.update(sql.toString(), new BeanPropertySqlParameterSource(entity));
    }

    @Override
    public <E> int delete(E entity) {
        Assert.notNull(entity, "delete with entity can not be null");
        TableMataDate mataDate = TableMataDate.forClass(entity.getClass());
        PropertyDescriptor keyPs = BeanUtils.getPropertyDescriptor(entity.getClass(), mataDate.getKeyProperty());
        Assert.notNull(keyPs, "can not find key property from " + entity.getClass().getName());
        Object keyValue = ReflectionUtils.invokeMethod(keyPs.getReadMethod(), entity);
        Assert.notNull(keyValue, "key value can not be null");
        String sql = SQL.init().DELETE_FROM(mataDate.getTableName())
                .WHERE(mataDate.getKeyColumn() + " = ?")
                .toString();
        return jdbcOperations.getJdbcOperations().update(sql, keyValue);
    }

    @Override
    public <E> int deleteById(Class<E> eClass, Long id) {
        Assert.notNull(id, "id can not be null");
        TableMataDate mataDate = TableMataDate.forClass(eClass);
        String sql = SQL.init().DELETE_FROM(mataDate.getTableName())
                .WHERE(mataDate.getKeyColumn() + " = ?")
                .toString();
        return jdbcOperations.getJdbcOperations().update(sql, id);
    }

    @Override
    public <E> E selectById(Class<E> eClass, Long id) {
        TableMataDate mataDate = TableMataDate.forClass(eClass);

        SQL sql = SQL.init().SELECT(mataDate.getBaseColumns())
                .FROM(mataDate.getTableName())
                .WHERE(mataDate.getKeyColumn() + " = :id");

        return selectForObject(sql, Collections.singletonMap("id", id), eClass);
    }

    @Override
    public <E> List<E> selectAll(Class<E> eClass) {
        TableMataDate mataDate = TableMataDate.forClass(eClass);
        String sql = SQL.init().SELECT(mataDate.getBaseColumns())
                .FROM(mataDate.getTableName())
                .toString();
        return jdbcOperations.query(sql, Collections.emptyMap(), AnnotationRowMapper.newInstance(eClass));
    }

    @Override
    public <E, R> E selectByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        Class<E> eClass = SerializedLambdaUtils.getLambdaClass(serializedFunction);
        String property = SerializedLambdaUtils.getProperty(serializedFunction);

        TableMataDate mataDate = TableMataDate.forClass(eClass);
        String column = mataDate.getFieldColumnMap().get(property);

        SQL sql = SQL.init().SELECT(mataDate.getBaseColumns())
                .FROM(mataDate.getTableName())
                .WHERE(column + " = :id");

        return selectForObject(sql, Collections.singletonMap("id", value), eClass);
    }

    @Override
    public <E, R> List<E> selectListByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        Class<E> eClass = SerializedLambdaUtils.getLambdaClass(serializedFunction);
        String property = SerializedLambdaUtils.getProperty(serializedFunction);

        TableMataDate mataDate = TableMataDate.forClass(eClass);
        String column = mataDate.getFieldColumnMap().get(property);

        String sql = SQL.init().SELECT(mataDate.getBaseColumns())
                .FROM(mataDate.getTableName())
                .WHERE(column + " = ?")
                .toString();
        return jdbcOperations.getJdbcOperations()
                .query(sql, new Object[]{value}, AnnotationRowMapper.newInstance(eClass));
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
    public <E, C> List<E> selectList(Class<E> eClass, C condition) {
        return selectListWithOrder(eClass, condition, null);
    }

    @Override
    public <E, C> List<E> selectListWithOrder(Class<E> eClass, C condition, OrderBy orderBy) {
        SQL sql = buildSelectSQL(eClass, condition, orderBy);
        return jdbcOperations.query(sql.toString(),
                new BeanPropertySqlParameterSource(condition), AnnotationRowMapper.newInstance(eClass));
    }

    @Override
    public <E> List<E> selectForList(SQL sql, Object args, Class<E> requiredType) {
        //noinspection unchecked
        SqlParameterSource parameterSource = args instanceof Map
                ? new MapSqlParameterSource((Map<String, ?>) args)
                : new BeanPropertySqlParameterSource(args);

        if (BeanUtils.isSimpleValueType(requiredType)) {
            return jdbcOperations.queryForList(sql.toString(), parameterSource, requiredType);
        }
        return jdbcOperations.query(sql.toString(), parameterSource, AnnotationRowMapper.newInstance(requiredType));
    }

    @Override
    public <E, C extends PageParam> List<E> selectPageList(Class<E> eClass, C condition) {
        SQL sql = buildSelectSQL(eClass, condition, null);
        return selectForPageList(sql, condition, eClass);
    }

    @Override
    public <E, C extends PageParam> List<E> selectPageListWithOrder(Class<E> eClass, C condition, OrderBy orderBy) {
        SQL sql = buildSelectSQL(eClass, condition, orderBy);
        return selectForPageList(sql, condition, eClass);
    }

    @Override
    public <E> List<E> selectForPageList(SQL sql, PageParam args, Class<E> requiredType) {
        int count = 0;
        if (args.isCount()) {
            String countSql = sql.countSql();
            count = jdbcOperations
                    .queryForObject(countSql, new BeanPropertySqlParameterSource(args), int.class);
            if (count <= 0) {
                return new PageList<>(args.getPageSize(), args.getPageNum(), 0);
            }
        }
        Dialect dialect = Dialect.valueOf(getDatabase());
        if (dialect == null) {
            return new PageList<>(args.getPageSize(), args.getPageNum(), 0);
        }
        String pageSql = dialect.getPageSql(sql.toString(), args.getPageSize(), args.getPageNum());
        List<E> rsList = jdbcOperations.query(pageSql,
                new BeanPropertySqlParameterSource(args), AnnotationRowMapper.newInstance(requiredType));
        return new PageList<>(rsList, args.getPageSize(), args.getPageNum(), count);
    }


    private <E, C> SQL buildSelectSQL(Class<E> eClass, C condition, OrderBy orderBy) {
        TableMataDate mataDate = TableMataDate.forClass(eClass);
        Map<String, String> fieldColumnMap = mataDate.getFieldColumnMap();

        SQL sql = SQL.init().SELECT(mataDate.getBaseColumns()).FROM(mataDate.getTableName());
        ReflectionUtils.doWithFields(condition.getClass(), field -> {
            Condition logicCondition = field.getAnnotation(Condition.class);
            String mappedProperty = logicCondition == null || StringUtils.isEmpty(logicCondition.property())
                    ? field.getName() : logicCondition.property();
            PropertyDescriptor entityPd = BeanUtils.getPropertyDescriptor(eClass, mappedProperty);
            if (entityPd == null || entityPd.getReadMethod() == null) {
                return;
            }
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(condition.getClass(), field.getName());
            if (pd == null || pd.getReadMethod() == null) {
                return;
            }
            Object value = ReflectionUtils.invokeMethod(pd.getReadMethod(), condition);
            if (StringUtils.isEmpty(value)) {
                return;
            }
            String column = fieldColumnMap.get(mappedProperty);
            String whereCondition = getWhereCondition(logicCondition, column, field.getName());
            sql.WHERE(whereCondition);
        });
        if (orderBy != null) {
            for (Map.Entry<String, String> entry : orderBy.getOrderByMap().entrySet()) {
                sql.ORDER_BY(fieldColumnMap.get(entry.getKey()) + entry.getValue());
            }
        }
        return sql;
    }

    private String getWhereCondition(Condition logicCondition, String column, String fieldName) {
        Logic logic = logicCondition == null ? Logic.EQ : logicCondition.logic();

        if (logic == Logic.NULL || logic == Logic.NOT_NULL) {
            return column + logic.getCode();
        }
        if (logic == Logic.IN || logic == Logic.NOT_IN) {
            return column + logic.getCode() + "(" + getNamedParam(fieldName) + ")";
        }
        return column + logic.getCode() + getNamedParam(fieldName);
    }


    private String getEqCondition(String column, String property) {
        return column + " = :" + property;
    }

    private String getNamedParam(String property) {
        return ":" + property;
    }


    public Database getDatabase() {
        if (StringUtils.isEmpty(database)) {
            database = jdbcOperations.getJdbcOperations().execute(
                    (ConnectionCallback<Database>) con -> Database.valueOf(con.getMetaData().getDatabaseProductName().toUpperCase())
            );
        }
        return database;
    }
}
