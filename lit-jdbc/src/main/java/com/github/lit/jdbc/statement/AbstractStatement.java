package com.github.lit.jdbc.statement;

import com.github.lit.exception.SysException;
import com.github.lit.jdbc.StatementExecutor;
import com.github.lit.jdbc.model.TableInfo;
import com.github.lit.jdbc.page.StatementPageHandler;
import com.github.lit.util.Assert;
import com.github.lit.util.ClassUtils;
import com.github.lit.util.NameUtils;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.jsqlparser.schema.Table;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * User : liulu
 * Date : 2017/6/4 9:31
 * version $Id: AbstractStatement.java, v 0.1 Exp $
 */
@NoArgsConstructor
public abstract class AbstractStatement implements Statement {

    protected Table table;

    protected TableInfo tableInfo;

    protected List<Object> params;

    protected boolean isNative = false;

    @Setter
    protected StatementExecutor executor;

    @Setter
    protected StatementPageHandler pageHandler;

    @Setter
    protected String dbName;


    protected AbstractStatement(Class<?> clazz) {
        params = new ArrayList<>();
        tableInfo = new TableInfo(clazz);
        table = new Table(tableInfo.getTableName());
    }


    protected String getColumn(String property) {
        Assert.notEmpty(property, "property must not be empty!");
        String column = tableInfo.getFieldColumnMap().get(property);
        return column == null || column.isEmpty() ? property : column;
    }

    protected <T, R> String getProperty(PropertyFunction<T, R> propertyFunction) {
        String getMethod = getSerializedLambda(propertyFunction).getImplMethodName();

        if (getMethod.startsWith("get")) {
            return NameUtils.getFirstLowerName(getMethod.substring(3));
        }
        if (getMethod.startsWith("is")) {
            return NameUtils.getFirstLowerName(getMethod.substring(2));
        }
        return NameUtils.getFirstLowerName(getMethod);

    }

    protected <T, R> Class<?> getPropertyClass(PropertyFunction<T, R> propertyFunction) {
        String implClass = getSerializedLambda(propertyFunction).getImplClass();
        if (implClass.contains("/")) {
            implClass = implClass.replace("/", ".");
        }
        return ClassUtils.forName(implClass);
    }

    private <T, R> SerializedLambda getSerializedLambda(PropertyFunction<T, R> propertyFunction) {
        try {
            Method writeReplace = propertyFunction.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(Boolean.TRUE);
            return (SerializedLambda) writeReplace.invoke(propertyFunction);
        } catch (Exception e) {
            throw new SysException(e);
        }
    }


}
