package com.github.lit.support.jdbc;

import com.github.lit.support.util.NameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-15 21:42
 */
public class AnnotationRowMapper<T> extends BeanPropertyRowMapper<T> {

    public AnnotationRowMapper() {
    }

    public AnnotationRowMapper(Class<T> mappedClass) {
        initialize(mappedClass);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Override
    protected void initialize(Class<T> mappedClass) {
        super.initialize(mappedClass);

        Field mappedFields = ReflectionUtils.findField(this.getClass(), "mappedFields");
        ReflectionUtils.makeAccessible(mappedFields);
        Field mappedProperties = ReflectionUtils.findField(this.getClass(), "mappedProperties");
        ReflectionUtils.makeAccessible(mappedProperties);

        Map<String, PropertyDescriptor> mappedFieldsMap = (Map) ReflectionUtils.getField(mappedFields, this);
        Set<String> mappedPropertiesSet = (Set<String>) ReflectionUtils.getField(mappedProperties, this);

        ReflectionUtils.doWithFields(mappedClass, field -> {
            if (Modifier.isStatic(field.getModifiers()) ||
                    field.isAnnotationPresent(Transient.class) ||
                    !BeanUtils.isSimpleValueType(field.getType())) {
                return;
            }
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(mappedClass, field.getName());
            if (pd != null && pd.getWriteMethod() != null) {
                Column column = AnnotationUtils.findAnnotation(field, Column.class);
                String columnName = column != null ? column.name().toLowerCase()
                        : NameUtils.getUnderLineName(field.getName());
                mappedFieldsMap.putIfAbsent(columnName, pd);
                String lowerPdName = pd.getName().toLowerCase();
                if (!lowerPdName.equals(columnName)) {
                    mappedFieldsMap.putIfAbsent(lowerPdName, pd);
                }
                mappedPropertiesSet.add(pd.getName());
            }
        });
    }


    /**
     * Static factory method to create a new {@code AnnotationRowMapper}
     * (with the mapped class specified only once).
     *
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T> AnnotationRowMapper<T> newInstance(Class<T> mappedClass) {
        return new AnnotationRowMapper<>(mappedClass);
    }


}
