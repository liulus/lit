package com.lit.support.data.jdbc;

import com.lit.support.data.LitRepository;
import com.lit.support.data.SQL;
import com.lit.support.data.domain.Page;
import com.lit.support.data.domain.Pageable;
import com.lit.support.data.domain.Sort;
import com.lit.support.util.SpringContextUtils;
import com.lit.support.util.lamabda.SerializedFunction;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
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

    public static final Map<String, JdbcExecutor> REPOSITORY_MAP = new ConcurrentHashMap<>(8);

    private String dataSourceId;
    protected Class<E> entityClass;

    protected DataSource dataSource;
    protected JdbcExecutor jdbcExecutor;

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

    @PostConstruct
    public void init() {
        initDataSource();
        JdbcExecutor cacheRepository = REPOSITORY_MAP.get(this.dataSourceId);
        if (cacheRepository != null) {
            this.jdbcExecutor = cacheRepository;
            return;
        }
        Collection<JdbcExecutor> jdbcRepositories = SpringContextUtils.getBeansOfType(JdbcExecutor.class).values();
        for (JdbcExecutor repository : jdbcRepositories) {
            JdbcExecutorImpl repositoryImpl = (JdbcExecutorImpl) repository;
            JdbcTemplate jdbcTemplate = (JdbcTemplate) repositoryImpl.getJdbcOperations().getJdbcOperations();
            DataSource jdbcTemplateDataSource = jdbcTemplate.getDataSource();
            if (Objects.equals(this.dataSource, jdbcTemplateDataSource)) {
                this.jdbcExecutor = repository;
                REPOSITORY_MAP.put(this.dataSourceId, repository);
                return;
            }
        }
        //
        this.jdbcExecutor = new JdbcExecutorImpl(this.dataSource);
        REPOSITORY_MAP.put(this.dataSourceId, jdbcExecutor);
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
        }
        throw new IllegalArgumentException("to many dataSource bean fund, please config one with lit.support.jdbc.data-source: [you dataSource bean id]");
    }

    @Override
    public int insert(E entity) {
        return jdbcExecutor.insert(entity);
    }

    @Override
    public int batchInsert(Collection<E> eList) {
        return jdbcExecutor.batchInsert(eList);
    }

    @Override
    public int update(E entity) {
        return jdbcExecutor.update(entity);
    }

    @Override
    public int updateSelective(E entity) {
        return jdbcExecutor.updateSelective(entity);
    }

    @Override
    public int deleteById(Long id) {
        return jdbcExecutor.deleteById(entityClass, id);
    }

    @Override
    public int deleteByIds(Collection<Long> ids) {
        return jdbcExecutor.deleteByIds(entityClass, ids);
    }

    @Override
    public E selectById(Long id) {
        return jdbcExecutor.selectById(entityClass, id);
    }

    @Override
    public List<E> selectByIds(Collection<Long> ids) {
        return jdbcExecutor.selectByIds(entityClass, ids);
    }

    @Override
    public List<E> selectAll() {
        return jdbcExecutor.selectAll(entityClass);
    }

    @Override
    public <R> E selectByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        return jdbcExecutor.selectByProperty(serializedFunction, value);
    }

    @Override
    public <R> List<E> selectListByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        return jdbcExecutor.selectListByProperty(serializedFunction, value);
    }

    @Override
    public <C> List<E> selectList(C condition) {
        return jdbcExecutor.selectList(entityClass, condition);
    }

    @Override
    public <C> List<E> selectListWithOrder(C condition, Sort sort) {
        return jdbcExecutor.selectListWithOrder(entityClass, condition, sort);
    }

    @Override
    public <C extends Pageable> Page<E> selectPageList(C condition) {
        return jdbcExecutor.selectPageList(entityClass, condition);
    }

    @Override
    public <T> T selectForObject(SQL sql, Object args, Class<T> requiredType) {
        return jdbcExecutor.selectForObject(sql, args, requiredType);
    }

    @Override
    public <T> List<T> selectForList(SQL sql, Object args, Class<T> requiredType) {
        return jdbcExecutor.selectForList(sql, args, requiredType);
    }

    @Override
    public <T> Page<T> selectForPageList(SQL sql, Pageable args, Class<T> requiredType) {
        return jdbcExecutor.selectForPageList(sql, args, requiredType);
    }

    @Override
    public int countAll() {
        return jdbcExecutor.count(entityClass);
    }

    @Override
    public <R> int countByProperty(SerializedFunction<E, R> serializedFunction, Object value) {
        return jdbcExecutor.countByProperty(serializedFunction, value);
    }
}
