package com.github.lit.support.data;

import com.github.lit.support.data.domain.Sort;
import com.github.lit.support.data.domain.TableMetaDate;
import com.github.lit.support.util.bean.BeanUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-21 19:57
 */
public abstract class SQLUtils {

    /**
     * MySQL 分页，参数1 ：第几条开始( offset ); 参数2：查询多少条(pageSize)
     */
    private static final String MYSQL_PAGE_SQL = "%s limit %d, %d ";

    /**
     * DB2 分页，参数1 ：第几条开始( offset ); 参数2：第几条为止(maxResult)
     */
    private static final String DB2_PAGE_SQL = "select * from ( select t.*, rownumber() over() rowid from ( %s ) t ) where rowid > %d ) and rowid <= %d ";

    /**
     * Oracle 分页，参数1：第几条为止(maxResult); 参数2 ：第几条开始( offset )
     */
    private static final String ORACLE_PAGE_SQL = "select * from (select t.*, rownum rowno from ( %s ) t where rownum <= %d ) where rowno > %d ";

    private SQLUtils() {
    }

    public static SQL insertSQL(Object entity, SQL.Type sqlType) {
        Objects.requireNonNull(entity, "insert with entity can not be null");
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
                sql.VALUES(entry.getValue(), getTokenParam(entry.getKey(), sqlType));
            }
        }
        return sql;
    }

    public static SQL updateSQL(Object entity, Boolean ignoreNull, SQL.Type sqlType) {
        Objects.requireNonNull(entity, "update with entity can not be null");
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
                    sql.SET(getEq(entry.getValue(), entry.getKey(), sqlType));
                }
            } else {
                sql.SET(getEq(entry.getValue(), entry.getKey(), sqlType));
            }
        }
        sql.WHERE(getEq(mataDate.getKeyColumn(), mataDate.getKeyProperty(), sqlType));
        return sql;
    }

    public static SQL deleteSQL(Class<?> eClass, SQL.Type sqlType) {
        TableMetaDate mataDate = TableMetaDate.forClass(eClass);
        return SQL.init().DELETE_FROM(mataDate.getTableName())
                .WHERE(getEq(mataDate.getKeyColumn(), mataDate.getKeyProperty(), sqlType));
    }


    public static SQL selectSQL(Class<?> eClass, Object condition, Sort sort, SQL.Type sqlType) {

        TableMetaDate metaDate = TableMetaDate.forClass(eClass);

        SQL sql = SQL.init().SELECT(metaDate.getBaseColumns()).FROM(metaDate.getTableName());
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
                sql.WHERE(mappedColumn + logic.getCode() + getIn(field.getName(), size, sqlType));
            } else {
                sql.WHERE(mappedColumn + logic.getCode() + getTokenParam(field.getName(), sqlType));
            }
        });
        if (sort != null) {
            for (Map.Entry<String, String> entry : sort.getOrderByMap().entrySet()) {
                String column = metaDate.getColumn(entry.getKey());
                sql.ORDER_BY(column + entry.getValue());
            }
        }
        return sql;
    }


    private static String getEq(String column, String param, SQL.Type sqlType) {
        return column + Logic.EQ.getCode() + getTokenParam(param, sqlType);
    }

    private static String getIn(String property, int size, SQL.Type sqlType) {
        if (sqlType == SQL.Type.JDBC) {
            return "( :" + property + ")";
        }
        if (sqlType == SQL.Type.MYBATIS) {
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
        throw new UnsupportedOperationException("unsupported operation build in with null SQL.Type");
    }


    private static String getTokenParam(String param, SQL.Type sqlType) {
        switch (sqlType) {
            case JDBC:
                return ":" + param;
            case MYBATIS:
                return "#{" + param + "}";
            default:
                throw new UnsupportedOperationException("unsupported operation build token param with null SQL.Type");
        }
    }

    public static String getPageSql(String dbName, String sql, int pageSize, int pageNum) {
        if (StringUtils.isEmpty(dbName)) {
            throw new UnsupportedOperationException("unsupported operation build page sql unknow database");
        }
        dbName = dbName.toUpperCase();
        switch (dbName) {
            case "DB2":
                return String.format(DB2_PAGE_SQL, sql, pageSize * (pageNum - 1), pageSize * pageNum);
            case "MYSQL":
                return String.format(MYSQL_PAGE_SQL, sql, pageSize * (pageNum - 1), pageSize);
            case "ORACLE":
                return String.format(ORACLE_PAGE_SQL, sql, pageSize * pageNum, pageSize * (pageNum - 1));
            default:
                throw new UnsupportedOperationException("unsupported operation build page sql for database: " + dbName);
        }
    }
}
