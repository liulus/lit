package com.lit.support.data.jdbc;

import com.lit.support.data.SQL;
import com.lit.support.data.annotation.LitRepository;
import com.lit.support.data.annotation.Param;
import com.lit.support.data.annotation.SELECT;
import com.lit.support.exception.BizException;
import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liulu
 * @version V1.0
 * @since 2020/9/28
 */
public class JdbcRepositoryFactoryBean<T> implements MethodInterceptor, BeanClassLoaderAware, FactoryBean<T> {

    private static final Map<String, JdbcExecutor> REPOSITORY_MAP = new ConcurrentHashMap<>(8);

    private static final Map<Class<?>, Object> TYPE_MAP = new ConcurrentHashMap<>();

    private ClassLoader beanClassLoader;

    private Class<T> repositoryInterface;

    protected Class<?> entityClass;

    @Setter
    private String dataSourceBeanName;

    @Resource
    private Map<String, DataSource> dataSourceMap;

    @Resource
    private List<JdbcExecutor> jdbcExecutors;

    public JdbcRepositoryFactoryBean() {
        //intentionally empty
    }

    public JdbcRepositoryFactoryBean(Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
        for (Type parent : repositoryInterface.getGenericInterfaces()) {
            ResolvableType parentType = ResolvableType.forType(parent);
            if (parentType.getRawClass() == JdbcRepository.class) {
                entityClass = parentType.getGeneric(0).getRawClass();
                break;
            }
        }
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        LitRepository repository = AnnotationUtils.findAnnotation(repositoryInterface, LitRepository.class);
        if (repository != null && StringUtils.hasText(repository.dataSource())) {
            this.dataSourceBeanName = repository.dataSource();
        }
        JdbcExecutor executor = getJdbcExecutor(this.dataSourceBeanName);

        Method method = methodInvocation.getMethod();
        Map<String, Object> originParams = getOriginParams(methodInvocation);


        SELECT select = AnnotationUtils.findAnnotation(method, SELECT.class);
        if (select != null) {
            String sql = select.value();
            if (StringUtils.isEmpty(sql)) {
                sql = select.sql();
            }
            if (StringUtils.isEmpty(sql)) {
                Class<?> type = select.type();
                if (type == Object.class) {
                    throw new BizException("至少指定一个sql");
                }
                Object obj = TYPE_MAP.get(type);
                if (obj == null) {
                    obj = BeanUtils.instantiateClass(type);
                    TYPE_MAP.put(type, obj);
                }
                String sqlMethodName = select.method();
                Method sqlMethod = ReflectionUtils.findMethod(type, sqlMethodName);
                if (sqlMethod == null) {
                    throw new BizException("找不到sqlMethod");
                }
                Parameter[] parameters = method.getParameters();
                List<Object> args = new ArrayList<>(parameters.length);
                for (Parameter parameter : parameters) {
                    Param param = AnnotationUtils.findAnnotation(parameter, Param.class);
                    if (param != null) {
                        String paramName = param.value();
                        args.add(originParams.get(paramName));
                    } else if (parameter.getType() == Map.class) {
                        args.add(originParams);
                    }
                }
                Object sqlObj = ReflectionUtils.invokeMethod(sqlMethod, obj, args.toArray());
                sql = String.valueOf(sqlObj);
            }

            executor.selectForObject(SQL.init(), originParams, method.getReturnType());

        }


        String methodName = method.getName();


        return executor;
    }


    public JdbcExecutor getJdbcExecutor(String dataSourceId) {
        DataSource dataSource = getDataSource(dataSourceId);
        JdbcExecutor cacheRepository = REPOSITORY_MAP.get(dataSourceId);
        if (cacheRepository != null) {
            return cacheRepository;
        }
        for (JdbcExecutor executor : jdbcExecutors) {
            JdbcExecutorImpl repositoryImpl = (JdbcExecutorImpl) executor;
            JdbcTemplate jdbcTemplate = (JdbcTemplate) repositoryImpl.getJdbcOperations().getJdbcOperations();
            DataSource jdbcTemplateDataSource = jdbcTemplate.getDataSource();
            if (Objects.equals(dataSource, jdbcTemplateDataSource)) {
                REPOSITORY_MAP.put(dataSourceId, executor);
                return executor;
            }
        }
        //
        JdbcExecutorImpl executor = new JdbcExecutorImpl(dataSource);
        REPOSITORY_MAP.put(dataSourceId, executor);
        return executor;
    }

    private DataSource getDataSource(String dataSourceId) {
        if (StringUtils.hasText(dataSourceId)) {
            return Optional.of(dataSourceId)
                    .map(dataSourceMap::get)
                    .orElseThrow(() -> new IllegalArgumentException("no dataSource bean with name " + dataSourceId));
        } else if (dataSourceMap.size() == 1) {
            this.dataSourceBeanName = dataSourceMap.keySet().iterator().next();
            return dataSourceMap.values().iterator().next();
        }
        throw new IllegalArgumentException("to many dataSource bean fund, please config one with lit.support.jdbc.data-source: [you dataSource bean id]");
    }


    private Map<String, Object> getOriginParams(MethodInvocation methodInvocation) {

        Map<String, Object> result = new HashMap<>();
        Method method = methodInvocation.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] arguments = methodInvocation.getArguments();
        int len = parameters.length;
        for (int i = 0; i < len; i++) {
            Parameter parameter = parameters[i];
            Param param = AnnotationUtils.findAnnotation(parameter, Param.class);
            if (param != null) {
                result.put(param.value(), arguments[i]);
                continue;
            }
            result.put("param", arguments[i]);
        }
        return result;
    }

    @Override
    public T getObject() throws Exception {
        //noinspection unchecked
        return (T) new ProxyFactory(repositoryInterface, this).getProxy(beanClassLoader);
    }

    @Override
    public Class<T> getObjectType() {
        return this.repositoryInterface;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

}
