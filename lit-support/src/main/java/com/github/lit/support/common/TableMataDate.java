package com.github.lit.support.common;

import com.github.lit.util.NameUtils;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author liulu
 * @version : v1.0
 * date : 7/24/18 11:31
 */
@Getter
public class TableMataDate implements Serializable {

    private static final int DEFAULT_CACHE_LIMIT = 128;

    @SuppressWarnings("serial")
    private static final Map<Class<?>, TableMataDate> TABLE_CACHE =
            new LinkedHashMap<Class<?>, TableMataDate>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Class<?>, TableMataDate> eldest) {
                    return size() > DEFAULT_CACHE_LIMIT;
                }
            };
    private static final long serialVersionUID = 7433386817779746930L;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 主键属性名
     */
    private String keyProperty;

    /**
     * 主键对应的列名
     */
    private String keyColumn;

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
        synchronized (TABLE_CACHE) {
            return TABLE_CACHE.computeIfAbsent(entityClass, TableMataDate::new);
        }
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
     * @param eClass 实体类的 class
     */
    private void initTableInfo(Class<?> eClass) {
        tableName = eClass.isAnnotationPresent(Table.class) ? eClass.getAnnotation(Table.class).name()
                : NameUtils.getUnderLineName(eClass.getSimpleName());

        Field[] fields = eClass.getDeclaredFields();
        for (Field field : fields) {

            // 过滤静态字段和有 @Transient 注解的字段
            if (Modifier.isStatic(field.getModifiers()) ||
                    field.isAnnotationPresent(Transient.class) ||
                    !BeanUtils.isSimpleValueType(field.getType())) {
                continue;
            }

            String property = field.getName();
            Column column = field.getAnnotation(Column.class);
            String columnName = column != null ? column.name() : NameUtils.getUnderLineName(property);

            // 主键信息 : 有 @Id 注解的字段，没有默认是 类名+Id
            if (field.isAnnotationPresent(Id.class) || (property.equalsIgnoreCase("id") && keyProperty == null)) {
                this.keyProperty = property;
                this.keyColumn = columnName;
            }
            // 将字段对应的列放到 map 中
            PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(eClass, property);
            if (descriptor != null && descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
                fieldColumnMap.put(property, columnName);
                fieldTypeMap.put(property, field.getType());
            }
        }
        fieldColumnMap = Collections.unmodifiableMap(fieldColumnMap);
        fieldTypeMap = Collections.unmodifiableMap(fieldTypeMap);
    }

}
