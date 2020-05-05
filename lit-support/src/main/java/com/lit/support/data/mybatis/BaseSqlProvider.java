package com.lit.support.data.mybatis;

import com.lit.support.data.Logic;
import com.lit.support.data.SQL;
import com.lit.support.data.SQLUtils;
import com.lit.support.data.domain.TableMetaDate;
import com.lit.support.util.lamabda.SerializedFunction;
import com.lit.support.util.lamabda.SerializedLambdaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author liulu
 * @version : v1.0
 * date : 7/24/18 11:04
 */
@Slf4j
public class BaseSqlProvider {

    public <E> String insert(E entity) {
        Assert.notNull(entity, "entity must not null");
        return SQLUtils.insertSQL(entity, SQL.Type.MYBATIS).toString();
    }

    public <E> String update(E entity) {
        Assert.notNull(entity, "entity must not null");

        return SQLUtils.updateSQL(entity, true, SQL.Type.MYBATIS).toString();
    }

    public String delete(ProviderContext context) {
        Class<?> entityClass = getEntityClass(context);
        return SQLUtils.deleteSQL(entityClass, SQL.Type.MYBATIS).toString();
    }

    public String selectById(ProviderContext context) {
        Class<?> entityClass = getEntityClass(context);
        TableMetaDate mataDate = TableMetaDate.forClass(entityClass);

        return new SQL().SELECT(mataDate.getBaseColumns())
                .FROM(mataDate.getTableName())
                .WHERE(mataDate.getKeyColumn() + Logic.EQ.getCode() + "#{" + mataDate.getKeyProperty() + "}")
                .toString();
    }

    public String selectByProperty(ProviderContext context, Map<String, Object> params) {
        SerializedFunction propertyFunction = (SerializedFunction) params.get("property");
        String property = SerializedLambdaUtils.getProperty(propertyFunction);
        Class<?> entityClass = getEntityClass(context);
        TableMetaDate mataDate = TableMetaDate.forClass(entityClass);
        String column = mataDate.getColumn(property);

        return new SQL().SELECT(mataDate.getBaseColumns())
                .FROM(mataDate.getTableName())
                .WHERE(column + Logic.EQ.getCode() + "#{value}")
                .toString();
    }

    public String selectByCondition(ProviderContext context, Object condition) {
        Class<?> entityClass = getEntityClass(context);
        return SQLUtils
                .selectSQL(entityClass, condition, null, SQL.Type.MYBATIS)
                .toString();
    }

    private Class<?> getEntityClass(ProviderContext context) {
        Class<?> mapperType = context.getMapperType();
        for (Type parent : mapperType.getGenericInterfaces()) {
            ResolvableType parentType = ResolvableType.forType(parent);
            if (parentType.getRawClass() == BaseMapper.class) {
                return parentType.getGeneric(0).getRawClass();
            }
        }
        return null;
    }
}
