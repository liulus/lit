package com.lit.support.data.jdbc;

import com.lit.support.data.LitRepository;
import com.lit.support.data.SQL;
import com.lit.support.data.SQLUtils;
import com.lit.support.page.Page;
import com.lit.support.page.Pageable;
import com.lit.support.page.Sort;
import com.lit.support.util.SpringContextUtils;
import com.lit.support.util.lamabda.SerializedFunction;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liulu
 * @version 1.0
 * created_at 2020/5/5
 */
public abstract class AbstractJdbcRepository<E> implements JdbcRepository<E> {

    private static final Map<String, JdbcExecutor> REPOSITORY_MAP = new ConcurrentHashMap<>(8);

    private String dataSourceId;
    protected Class<E> entityClass;

    private DataSource dataSource;
    private JdbcExecutor jdbcExecutor;

    public AbstractJdbcRepository() {
        ResolvableType type = ResolvableType.forType(getClass().getGenericSuperclass());
        for (int i = 0; i < 5; i++) {
            ResolvableType[] generics = type.getGenerics();
            if (generics.length == 1) {
                //noinspection unchecked
                entityClass = (Class<E>) generics[0].getRawClass();
                break;
            }
            type = ResolvableType.forType(Objects.requireNonNull(type.getRawClass()).getGenericSuperclass());
        }
    }

    public JdbcExecutor getJdbcExecutor() {
        if (this.jdbcExecutor != null) {
            return this.jdbcExecutor;
        }
        synchronized (this) {
            if (this.jdbcExecutor == null) {
                this.jdbcExecutor = initJdbcExecutor();
            }
        }
        return this.jdbcExecutor;
    }

    public JdbcExecutor initJdbcExecutor() {
        initDataSource();
        JdbcExecutor cacheRepository = REPOSITORY_MAP.get(this.dataSourceId);
        if (cacheRepository != null) {
            return cacheRepository;
        }
        Collection<JdbcExecutor> jdbcRepositories = SpringContextUtils.getBeansOfType(JdbcExecutor.class).values();
        for (JdbcExecutor repository : jdbcRepositories) {
            JdbcExecutorImpl repositoryImpl = (JdbcExecutorImpl) repository;
            JdbcTemplate jdbcTemplate = (JdbcTemplate) repositoryImpl.getJdbcOperations().getJdbcOperations();
            DataSource jdbcTemplateDataSource = jdbcTemplate.getDataSource();
            if (Objects.equals(this.dataSource, jdbcTemplateDataSource)) {
                REPOSITORY_MAP.put(this.dataSourceId, repository);
                return repository;
            }
        }
        //
        JdbcExecutorImpl executor = new JdbcExecutorImpl(this.dataSource);
        REPOSITORY_MAP.put(this.dataSourceId, executor);
        return executor;
    }

    private void initDataSource() {
        Map<String, DataSource> dataSourceMap = SpringContextUtils.getBeansOfType(DataSource.class);
        LitRepository repository = AnnotationUtils.findAnnotation(getClass(), LitRepository.class);
        if (repository != null && StringUtils.hasText(repository.dataSource())) {
            this.dataSourceId = repository.dataSource();
        } else {
            this.dataSourceId = SpringContextUtils.getProperty("lit.support.jdbc.data-source");
        }
        if (StringUtils.hasText(dataSourceId)) {
            this.dataSource = Optional.of(dataSourceId)
                    .map(dataSourceMap::get)
                    .orElseThrow(() -> new IllegalArgumentException("no dataSource bean with name " + dataSourceId));
            return;
        }
        if (dataSourceMap.size() == 1) {
            this.dataSourceId = dataSourceMap.keySet().iterator().next();
            this.dataSource = dataSourceMap.values().iterator().next();
            return;
        }
        throw new IllegalArgumentException("to many dataSource bean fund, please config one with lit.support.jdbc.data-source: [you dataSource bean id]");
    }

    @Override
    public int insert(E entity) {
        return getJdbcExecutor().insert(entity);
    }

    @Override
    public int batchInsert(Collection<E> eList) {
        return getJdbcExecutor().batchInsert(eList);
    }

    @Override
    public int update(E entity) {
        return getJdbcExecutor().update(entity);
    }

    @Override
    public int updateSelective(E entity) {
        return getJdbcExecutor().updateSelective(entity);
    }

    @Override
    public int deleteById(Long id) {
        return getJdbcExecutor().deleteById(entityClass, id);
    }

    @Override
    public int deleteByIds(Collection<Long> ids) {
        return getJdbcExecutor().deleteByIds(entityClass, ids);
    }

    @Override
    public E selectById(Long id) {
        return getJdbcExecutor().selectById(entityClass, id);
    }

    @Override
    public List<E> selectByIds(Collection<Long> ids) {
        return getJdbcExecutor().selectByIds(entityClass, ids);
    }

    @Override
    public List<E> selectAll() {
        return getJdbcExecutor().selectAll(entityClass);
    }

    @Override
    public <R> E selectByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        return getJdbcExecutor().selectByProperty(serializedFunction, value);
    }

    @Override
    public <R> List<E> selectListByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        return getJdbcExecutor().selectListByProperty(serializedFunction, value);
    }

    @Override
    public <C> List<E> selectList(C condition) {
        return getJdbcExecutor().selectList(entityClass, condition);
    }

    @Override
    public <C> List<E> selectListWithOrder(C condition, Sort sort) {
        return getJdbcExecutor().selectListWithOrder(entityClass, condition, sort);
    }

    @Override
    public <C extends Pageable> Page<E> selectPageList(C condition) {
        SQL sql = buildPageSQL(condition);
        return getJdbcExecutor().selectForPageList(sql, condition, entityClass);
    }

    protected SQL buildPageSQL(Pageable condition) {
        return SQLUtils.selectSQL(entityClass, condition, condition.getSort(), SQL.Type.JDBC);
    }

    @Override
    public int countAll() {
        return getJdbcExecutor().count(entityClass);
    }

    @Override
    public <R> int countByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        return getJdbcExecutor().countByProperty(serializedFunction, value);
    }

    protected SQL baseSelectSQL() {
        return SQL.baseSelect(entityClass);
    }

    protected  <C> E selectSingle(SQL sql, C condition) {
        return getJdbcExecutor().selectForObject(sql, condition, entityClass);
    }

    protected <C> List<E> selectList(SQL sql, C condition) {
        return getJdbcExecutor().selectForList(sql, condition, entityClass);
    }
}
