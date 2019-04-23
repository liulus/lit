package com.github.lit.support.jdbc;

import com.github.lit.support.page.OrderBy;
import com.github.lit.support.page.PageInfo;
import com.github.lit.support.page.PageResult;
import com.github.lit.support.page.Pageable;
import com.github.lit.support.sql.Database;
import com.github.lit.support.sql.SQL;
import com.github.lit.support.sql.SQLUtils;
import com.github.lit.support.sql.TableMetaDate;
import com.github.lit.support.sql.dialect.Dialect;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-10 14:28
 */
@NoArgsConstructor
public class JdbcRepositoryImpl implements JdbcRepository {

    private static final String OPEN_TOKEN = ":";

    @Setter
    private Database database;

    @Getter
    @Setter
    private NamedParameterJdbcOperations jdbcOperations;

    public Database getDatabase() {
        if (StringUtils.isEmpty(database)) {
            database = jdbcOperations.getJdbcOperations().execute(
                    (ConnectionCallback<Database>) con -> Database.valueOf(con.getMetaData().getDatabaseProductName().toUpperCase())
            );
        }
        return database;
    }

    public JdbcRepositoryImpl(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public <E> int insert(E entity) {
        Assert.notNull(entity, "insert with entity can not be null");
        Class<?> entityClass = entity.getClass();
        TableMetaDate metaDate = TableMetaDate.forClass(entityClass);
        SQL sql = SQLUtils.insertSQL(entity, this::getNamedParam);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int insert = jdbcOperations.update(sql.toString(), new BeanPropertySqlParameterSource(entity), keyHolder);
        PropertyDescriptor keyPs = BeanUtils.getPropertyDescriptor(entityClass, metaDate.getKeyProperty());
        ReflectionUtils.invokeMethod(keyPs.getWriteMethod(), entity, keyHolder.getKey().longValue());
        return insert;
    }

    @Override
    public <E> int batchInsert(Collection<E> eList) {
        if (CollectionUtils.isEmpty(eList)) {
            return 0;
        }
        E entity = eList.iterator().next();
        SQL sql = SQLUtils.insertSQL(entity, this::getNamedParam);
        BeanPropertySqlParameterSource[] parameterSources = eList.stream()
                .map(BeanPropertySqlParameterSource::new)
                .collect(Collectors.toList())
                .toArray(new BeanPropertySqlParameterSource[eList.size()]);
        int[] updateResult = jdbcOperations.batchUpdate(sql.toString(), parameterSources);

        int row = 0;
        for (int res : updateResult) {
            row += res;
        }
        return row;
    }


    @Override
    public <E> int update(E entity) {
        SQL sql = SQLUtils.updateSQL(entity, false, this::getNamedParam);
        return jdbcOperations.update(sql.toString(), new BeanPropertySqlParameterSource(entity));
    }

    @Override
    public <E> int updateSelective(E entity) {
        SQL sql = SQLUtils.updateSQL(entity, true, this::getNamedParam);
        return jdbcOperations.update(sql.toString(), new BeanPropertySqlParameterSource(entity));
    }

    @Override
    public <E> int delete(E entity) {
        Assert.notNull(entity, "delete with entity can not be null");
        TableMetaDate metaDate = TableMetaDate.forClass(entity.getClass());
        PropertyDescriptor keyPs = BeanUtils.getPropertyDescriptor(entity.getClass(), metaDate.getKeyProperty());
        Assert.notNull(keyPs, "can not find key property from " + entity.getClass().getName());
        Object keyValue = ReflectionUtils.invokeMethod(keyPs.getReadMethod(), entity);
        Assert.notNull(keyValue, "key value can not be null");

        SQL sql = SQLUtils.deleteSQL(entity.getClass(), this::getNamedParam);

        return jdbcOperations.update(sql.toString(), Collections.singletonMap(metaDate.getKeyProperty(), keyValue));
    }

    @Override
    public <E> int deleteById(Class<E> eClass, Long id) {
        Assert.notNull(id, "id can not be null");
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);
        SQL sql = SQLUtils.deleteSQL(eClass, this::getNamedParam);
        return jdbcOperations.update(sql.toString(), Collections.singletonMap(metaDate.getKeyProperty(), id));
    }

    @Override
    public <E> int deleteByIds(Class<E> eClass, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);
        String sql = SQL.init().DELETE_FROM(metaDate.getTableName())
                .WHERE(metaDate.getKeyColumn() + " in (:ids)")
                .toString();
        return jdbcOperations.update(sql, Collections.singletonMap("ids", ids));
    }

    @Override
    public <E> E selectById(Class<E> eClass, Long id) {
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);

        SQL sql = SQL.init().SELECT(metaDate.getAllColumns())
                .FROM(metaDate.getTableName())
                .WHERE(metaDate.getKeyColumn() + " = :id");

        return selectForObject(sql, Collections.singletonMap("id", id), eClass);
    }

    @Override
    public <E> List<E> selectByIds(Class<E> eClass, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);

        SQL sql = SQL.init().SELECT(metaDate.getAllColumns())
                .FROM(metaDate.getTableName())
                .WHERE(metaDate.getKeyColumn() + " in (:ids)");

        return selectForList(sql, Collections.singletonMap("ids", ids), eClass);
    }

    @Override
    public <E> List<E> selectAll(Class<E> eClass) {
        TableMetaDate metaDate = TableMetaDate.forClass(eClass);
        String sql = SQL.init().SELECT(metaDate.getAllColumns())
                .FROM(metaDate.getTableName())
                .toString();
        return jdbcOperations.query(sql, Collections.emptyMap(), AnnotationRowMapper.newInstance(eClass));
    }

    @Override
    public <E, R> E selectByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        Class<E> eClass = SerializedLambdaUtils.getLambdaClass(serializedFunction);
        String property = SerializedLambdaUtils.getProperty(serializedFunction);

        TableMetaDate metaDate = TableMetaDate.forClass(eClass);
        String column = metaDate.getFieldColumnMap().get(property);

        SQL sql = SQL.init().SELECT(metaDate.getAllColumns())
                .FROM(metaDate.getTableName())
                .WHERE(column + " = :id");

        return selectForObject(sql, Collections.singletonMap("id", value), eClass);
    }

    @Override
    public <E, R> List<E> selectListByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        Class<E> eClass = SerializedLambdaUtils.getLambdaClass(serializedFunction);
        String property = SerializedLambdaUtils.getProperty(serializedFunction);

        TableMetaDate metaDate = TableMetaDate.forClass(eClass);
        String column = metaDate.getFieldColumnMap().get(property);

        String sql = SQL.init().SELECT(metaDate.getAllColumns())
                .FROM(metaDate.getTableName())
                .WHERE(column + " = :id")
                .toString();
        return jdbcOperations.query(sql, Collections.singletonMap("id", value), AnnotationRowMapper.newInstance(eClass));
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

        SQL sql = SQLUtils.selectSQL(eClass, condition, orderBy, this::getNamedParam, SQLUtils::jdbcIn);
        return jdbcOperations.query(sql.toString(),
                new BeanPropertySqlParameterSource(condition), AnnotationRowMapper.newInstance(eClass));
    }

    @Override
    public <E> List<E> selectForList(SQL sql, Object args, Class<E> requiredType) {
        if (args == null) {
            args = Collections.emptyMap();
        }
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
    public <E, C extends Pageable> PageResult<E> selectPageList(Class<E> eClass, C condition) {

        SQL sql = SQLUtils.selectSQL(eClass, condition, condition.getOrderBy(), this::getNamedParam, SQLUtils::jdbcIn);
        return selectForPageList(sql, condition, eClass);
    }

//    @Override
//    public <E, C extends Pageable> List<E> selectPageListWithOrder(Class<E> eClass, C condition, OrderBy orderBy) {
//        SQL sql = buildSelectSQL(eClass, condition, orderBy);
//        return selectForPageList(sql, condition, eClass);
//    }

    @Override
    public <E> PageResult<E> selectForPageList(SQL sql, Pageable args, Class<E> requiredType) {
        Integer count = 0;
        if (args.isCount()) {
            String countSql = sql.countSql();
            count = jdbcOperations.queryForObject(countSql, new BeanPropertySqlParameterSource(args), int.class);
            if (count <= 0) {
                return PageResult.emptyPage();
            }
        }
        Dialect dialect = Dialect.valueOf(getDatabase());
        if (dialect == null) {
            return PageResult.emptyPage();
        }
        String pageSql = dialect.getPageSql(sql.toString(), args.getPageSize(), args.getPageNum());
        List<E> rsList = jdbcOperations.query(pageSql,
                new BeanPropertySqlParameterSource(args), AnnotationRowMapper.newInstance(requiredType));
        PageResult<E> result = new PageResult<>();
        result.setData(rsList);
        result.setPageInfo(new PageInfo(args.getPageSize(), args.getPageNum(), count));
        return result;
    }

    @Override
    public <E> int countAll(Class<E> eClass) {
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
                .WHERE(metaDate.getColumn(property) + " = :id");

        return selectForObject(sql, Collections.singletonMap("id", value), int.class);
    }


    private String getNamedParam(String property) {
        return ":" + property;
    }

}
