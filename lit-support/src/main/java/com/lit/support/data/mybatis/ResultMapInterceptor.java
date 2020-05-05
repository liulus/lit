package com.lit.support.data.mybatis;

import com.lit.support.data.domain.TableMetaDate;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * User : liulu
 * Date : 2018/7/11 12:42
 * version $Id: ResultMapInterceptor.java, v 0.1 Exp $
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class ResultMapInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!(invocation.getTarget() instanceof Executor)) {
            return invocation.proceed();
        }
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];

        // xml sql 不做处理
        if (ms.getResource().contains(".xml")) {
            return invocation.proceed();
        }
        ResultMap resultMap = ms.getResultMaps().iterator().next();
        if (!CollectionUtils.isEmpty(resultMap.getResultMappings())) {
            return invocation.proceed();
        }
        Class<?> mapType = resultMap.getType();
        if (ClassUtils.isAssignable(mapType, Collection.class)) {
            return invocation.proceed();
        }
        TableMetaDate mataDate = TableMetaDate.forClass(mapType);
        Map<String, Class<?>> fieldTypeMap = mataDate.getFieldTypeMap();
        //
        List<ResultMapping> resultMappings = new ArrayList<>(fieldTypeMap.size());
        for (Map.Entry<String, String> entry : mataDate.getFieldColumnMap().entrySet()) {
            ResultMapping resultMapping = new ResultMapping.Builder(ms.getConfiguration(), entry.getKey(), entry.getValue(), fieldTypeMap.get(entry.getKey())).build();
            resultMappings.add(resultMapping);
        }
        ResultMap newRm = new ResultMap.Builder(ms.getConfiguration(), resultMap.getId(), mapType, resultMappings).build();

        Field field = ReflectionUtils.findField(MappedStatement.class, "resultMaps");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, ms, Collections.singletonList(newRm));

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
