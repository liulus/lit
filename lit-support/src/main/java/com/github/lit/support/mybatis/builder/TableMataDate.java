package com.github.lit.support.mybatis.builder;

import com.github.lit.support.mybatis.annotation.Column;
import com.github.lit.support.mybatis.annotation.Id;
import com.github.lit.support.mybatis.annotation.Table;
import com.github.lit.support.mybatis.annotation.Transient;
import com.github.lit.util.NameUtils;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liulu
 * @version : v1.0
 * date : 7/24/18 11:31
 */
@Getter
public class TableMataDate {

    private static final Map<Class<?>, TableMataDate> TABLE_CACHE = new ConcurrentHashMap<>(64);

    /**
     * 表名
     */
    private String tableName;

    /**
     * 主键属性名
     */
    private String pkProperty;

    /**
     * 主键对应的列名
     */
    private String pkColumn;

    /**
     * 属性名和字段名映射关系的 map
     */
    private Map<String, String> fieldColumnMap;

    /**
     * 字段类型
     */
    private Map<String, Class<?>> fieldTypeMap;

    private TableMataDate(Class<?> clazz) {
        fieldColumnMap = new HashMap<>();
        fieldTypeMap = new HashMap<>();
        initTableInfo(clazz);
    }


    public static TableMataDate forClass(Class<?> entityClass) {
        TableMataDate tableMataDate = TABLE_CACHE.get(entityClass);
        if (tableMataDate == null) {
            tableMataDate = new TableMataDate(entityClass);
            TABLE_CACHE.put(entityClass, tableMataDate);
        }

        return tableMataDate;
    }

    public String getBaseColumns() {
        Collection<String> columns = fieldColumnMap.values();
        if (CollectionUtils.isEmpty(columns)) {
            return "";
        }
        Iterator<String> iterator = columns.iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            String next = iterator.next();
            sb.append(tableName).append(".").append(next);
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * 根据注解初始化表信息，
     *
     * @param clazz 实体类的 class
     */
    private void initTableInfo(Class<?> clazz) {
        tableName = clazz.isAnnotationPresent(Table.class) ? clazz.getAnnotation(Table.class).name()
                : NameUtils.getUnderLineName(clazz.getSimpleName());

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            // 过滤静态字段和有 @Transient 注解的字段
            if (Modifier.isStatic(field.getModifiers()) ||
                    field.isAnnotationPresent(Transient.class) ||
                    !BeanUtils.isSimpleValueType(field.getType())) {
                continue;
            }

            String property = field.getName();
            Column column = field.getAnnotation(Column.class);
            String columnName = column != null ? column.name().toLowerCase() : NameUtils.getUnderLineName(property);

            // 主键信息 : 有 @Id 注解的字段，没有默认是 类名+Id
            if (field.isAnnotationPresent(Id.class) || (property.equalsIgnoreCase("id") && pkProperty == null)) {
                pkProperty = property;
                pkColumn = columnName;
            }
            // 将字段对应的列放到 map 中
            PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(clazz, property);
            if (descriptor != null && descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
                fieldColumnMap.put(property, columnName);
                fieldTypeMap.put(property, field.getType());
            }
        }
    }

}
