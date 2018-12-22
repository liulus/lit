package com.github.lit.support.sql;

import com.github.lit.support.annotation.Condition;
import com.github.lit.support.page.OrderBy;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-21 19:57
 */
public abstract class SQLUtils {


    public static SQL insertSQL(Object entity, Function<String, String> namedParam) {
        Assert.notNull(entity, "insert with entity can not be null");
        Class<?> eClass = entity.getClass();
        TableMetaDate mataDate = TableMetaDate.forClass(eClass);
        Map<String, String> fieldColumnMap = mataDate.getFieldColumnMap();
        SQL sql = SQL.init().INSERT_INTO(mataDate.getTableName());
        for (Map.Entry<String, String> entry : fieldColumnMap.entrySet()) {
            // 忽略主键
            PropertyDescriptor ps = BeanUtils.getPropertyDescriptor(eClass, entry.getKey());
            if (Objects.equals(entry.getKey(), mataDate.getKeyProperty())
                    || ps == null || ps.getReadMethod() == null) {
                continue;
            }
            Object value = ReflectionUtils.invokeMethod(ps.getReadMethod(), entity);
            if (!StringUtils.isEmpty(value)) {
                sql.VALUES(entry.getValue(), namedParam.apply(entry.getKey()));
            }
        }
        return sql;
    }

    public static SQL updateSQL(Object entity, Boolean ignoreNull, Function<String, String> namedParam) {
        Assert.notNull(entity, "update with entity can not be null");
        Class<?> entityClass = entity.getClass();
        TableMetaDate mataDate = TableMetaDate.forClass(entityClass);
        Map<String, String> fieldColumnMap = mataDate.getFieldColumnMap();

        SQL sql = SQL.init().UPDATE(mataDate.getTableName());
        for (Map.Entry<String, String> entry : fieldColumnMap.entrySet()) {
            // 忽略主键
            PropertyDescriptor ps = BeanUtils.getPropertyDescriptor(entityClass, entry.getKey());
            if (Objects.equals(entry.getKey(), mataDate.getKeyProperty())
                    || ps == null || ps.getReadMethod() == null) {
                continue;
            }
            if (ignoreNull) {
                Object value = ReflectionUtils.invokeMethod(ps.getReadMethod(), entity);
                if (value != null) {
                    sql.SET(getEq(entry.getValue(), entry.getKey(), namedParam));
                }
            } else {
                sql.SET(getEq(entry.getValue(), entry.getKey(), namedParam));
            }
        }
        sql.WHERE(getEq(mataDate.getKeyColumn(), mataDate.getKeyProperty(), namedParam));
        return sql;
    }

    public static SQL deleteSQL(Class<?> eClass, Function<String, String> namedParam) {
        TableMetaDate mataDate = TableMetaDate.forClass(eClass);
        return SQL.init().DELETE_FROM(mataDate.getTableName())
                .WHERE(getEq(mataDate.getKeyColumn(), mataDate.getKeyProperty(), namedParam));
    }


    public static SQL selectSQL(Class<?> eClass, Object condition,
                                OrderBy orderBy,
                                Function<String, String> namedParam,
                                BiFunction<String, Integer, String> inFunction) {

        TableMetaDate metaDate = TableMetaDate.forClass(eClass);

        SQL sql = SQL.init().SELECT(metaDate.getAllColumns()).FROM(metaDate.getTableName());
        ReflectionUtils.doWithFields(condition.getClass(), field -> {
            Condition logicCondition = field.getAnnotation(Condition.class);

            String mappedColumn = logicCondition == null || StringUtils.isEmpty(logicCondition.property())
                    ? metaDate.getColumn(field.getName()) : metaDate.getColumn(logicCondition.property());
            if (!metaDate.containsColumn(mappedColumn)) {
                return;
            }
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(condition.getClass(), field.getName());
            if (pd == null || pd.getReadMethod() == null) {
                return;
            }
            Logic logic = logicCondition == null ? Logic.EQ : logicCondition.logic();
            Object value = ReflectionUtils.invokeMethod(pd.getReadMethod(), condition);
            if (StringUtils.isEmpty(value)) {
                if (logic == Logic.NULL || logic == Logic.NOT_NULL) {
                    sql.WHERE(mappedColumn + logic.getCode());
                }
                return;
            }
            if ((logic == Logic.IN || logic == Logic.NOT_IN) && value instanceof Collection) {
                int size = ((Collection) value).size();
                sql.WHERE(mappedColumn + logic.getCode() + inFunction.apply(field.getName(), size));
            } else {
                sql.WHERE(mappedColumn + logic.getCode() + namedParam.apply(field.getName()));
            }
        });
        if (orderBy != null) {
            for (Map.Entry<String, String> entry : orderBy.getOrderByMap().entrySet()) {
                String column = metaDate.getColumn(entry.getKey());
                sql.ORDER_BY(column + entry.getValue());
            }
        }
        return sql;
    }


    private static String getEq(String column, String param, Function<String, String> namedParam) {
        return column + Logic.EQ.getCode() + namedParam.apply(param);
    }

    public static String mybadisIn(String property, int size) {
        MessageFormat messageFormat = new MessageFormat("#'{'" + property + "[{0}]}");
        StringBuilder sb = new StringBuilder(" (");
        for (int i = 0; i < size; i++) {
            sb.append(messageFormat.format(new Object[]{i}));
            if (i != size - 1) {
                sb.append(", ");
            }
        }
        return sb.append(")").toString();
    }

    public static String jdbcIn(String property, int size) {
        return "( :" + property + ")";
    }


}
