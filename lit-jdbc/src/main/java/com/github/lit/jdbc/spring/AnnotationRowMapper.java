package com.github.lit.jdbc.spring;

import com.github.lit.commons.bean.BeanUtils;
import com.github.lit.commons.util.ClassUtils;
import com.github.lit.commons.util.NameUtils;
import com.github.lit.jdbc.annotation.Column;
import com.github.lit.jdbc.annotation.Transient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * User : liulu
 * Date : 2016-9-25 16:20
 * version $Id: AnnotationRowMapper.java, v 0.1 Exp $
 */
@Slf4j
public class AnnotationRowMapper<T> implements RowMapper<T> {


    /**
     * The class we are mapping to
     */
    private Class<T> mappedClass;

    /**
     * Whether we're defaulting primitives when mapping a null value
     */
    private boolean primitivesDefaultedForNullValue = false;

    /**
     * Map of the fields we provide mapping for
     */
    private Map<String, PropertyDescriptor> mappedFields;

    /**
     * Create a new {@code AnnotationRowMapper} for bean-style configuration.
     *
     * @see #setMappedClass
     */
    public AnnotationRowMapper() {
    }

    /**
     * Create a new {@code AnnotationRowMapper}, accepting unpopulated
     * properties in the target bean.
     * <p>Consider using the {@link #newInstance} factory method instead,
     * which allows for specifying the mapped type once only.
     *
     * @param mappedClass the class that each row should be mapped to
     */
    public AnnotationRowMapper(Class<T> mappedClass) {
        initialize(mappedClass);
    }

    /**
     * Set the class that each row should be mapped to.
     *
     * @param mappedClass the class that each row should be mapped to
     */
    public void setMappedClass(Class<T> mappedClass) {
        if (this.mappedClass == null) {
            initialize(mappedClass);
        } else {
            if (this.mappedClass != mappedClass) {
                throw new InvalidDataAccessApiUsageException("The mapped class can not be reassigned to map to " +
                        mappedClass + " since it is already providing mapping for " + this.mappedClass);
            }
        }
    }

    /**
     * Get the class that we are mapping to.
     *
     * @return the mapped class
     */
    public final Class<T> getMappedClass() {
        return this.mappedClass;
    }

    /**
     * Set whether we're defaulting Java primitives in the case of mapping a null value
     * from corresponding database fields.
     * <p>Default is {@code false}, throwing an exception when nulls are mapped to Java primitives.
     *
     * @param primitivesDefaultedForNullValue 基础类型为null
     */
    public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
        this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
    }

    /**
     * Return whether we're defaulting Java primitives in the case of mapping a null value
     * from corresponding database fields.
     *
     * @return primitivesDefaultedForNullValue
     */
    public boolean isPrimitivesDefaultedForNullValue() {
        return this.primitivesDefaultedForNullValue;
    }

    /**
     * Initialize the mapping metadata for the given class.
     *
     * @param mappedClass the mapped class
     */
    protected void initialize(Class<T> mappedClass) {

        this.mappedClass = mappedClass;
        this.mappedFields = new HashMap<>();
        addMappedField(mappedClass);
    }


    /**
     * Extract the values for all columns in the current row.
     * <p>Utilizes public setters and result set metadata.
     *
     * @see java.sql.ResultSetMetaData
     */
    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Assert.state(this.mappedClass != null, "Mapped class was not specified");
        T mappedObject = ClassUtils.newInstance(mappedClass);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            String field = column.replaceAll(" ", "").toLowerCase();
            PropertyDescriptor pd = this.mappedFields.get(field);
            if (pd != null) {
                try {
                    Object value = getColumnValue(rs, index, pd);
                    if (rowNumber == 0 && log.isDebugEnabled()) {
                        log.debug("Mapping column '" + column + "' to property '" + pd.getName() +
                                "' of type '" + org.springframework.util.ClassUtils.getQualifiedName(pd.getPropertyType()) + "'");
                    }
                    try {
                        bw.setPropertyValue(pd.getName(), value);
                    } catch (TypeMismatchException ex) {
                        if (value == null && this.primitivesDefaultedForNullValue) {
                            if (log.isDebugEnabled()) {
                                log.debug("Intercepted TypeMismatchException for row " + rowNumber +
                                        " and column '" + column + "' with null value when setting property '" +
                                        pd.getName() + "' of type '" +
                                        org.springframework.util.ClassUtils.getQualifiedName(pd.getPropertyType()) +
                                        "' on object: " + mappedObject, ex);
                            }
                        } else {
                            throw ex;
                        }
                    }
                } catch (NotWritablePropertyException ex) {
                    throw new DataRetrievalFailureException(
                            "Unable to map column '" + column + "' to property '" + pd.getName() + "'", ex);
                }
            } else {
                // No PropertyDescriptor found
                if (rowNumber == 0 && log.isDebugEnabled()) {
                    log.debug("No property found for column '" + column + "' mapped to field '" + field + "'");
                }
            }
        }

        return mappedObject;
    }

    /**
     * Retrieve a JDBC object value for the specified column.
     * <p>The default implementation calls
     * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}.
     * Subclasses may override this to check specific value types upfront,
     * or to post-process values return from {@code getResultSetValue}.
     *
     * @param rs    is the ResultSet holding the data
     * @param index is the column index
     * @param pd    the bean property that each result object is expected to match
     *              (or {@code null} if none specified)
     * @return the Object value
     * @throws SQLException in case of extraction failure
     * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)
     */
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
    }


    /**
     * Static factory method to create a new {@code BeanPropertyRowMapper}
     * (with the mapped class specified only once).
     *
     * @param mappedClass the class that each row should be mapped to
     * @param <T> the class
     * @return AnnotationRowMapper
     */
    public static <T> AnnotationRowMapper<T> newInstance(Class<T> mappedClass) {
        return new AnnotationRowMapper<>(mappedClass);
    }

    /**
     * 当 返回类型不是实体类时, 增加实体类上的映射关系
     *
     * @param clazz the class
     */
    public void addMappedField(Class<?> clazz) {

        if (clazz == null) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, field.getName());
            if (pd != null && pd.getReadMethod() != null && pd.getWriteMethod() != null) {
                String columnName = column != null ? column.name().toLowerCase() : NameUtils.getUnderLineName(field.getName());
                mappedFields.putIfAbsent(columnName, pd);

                String lowerPdName = pd.getName().toLowerCase();
                if (!lowerPdName.equals(columnName)) {
                    mappedFields.putIfAbsent(lowerPdName, pd);

                }
            }
        }

    }

}
