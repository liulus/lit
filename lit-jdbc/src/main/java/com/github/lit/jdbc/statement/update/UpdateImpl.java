package com.github.lit.jdbc.statement.update;

import com.github.lit.commons.bean.BeanUtils;
import com.github.lit.jdbc.model.StatementContext;
import com.github.lit.jdbc.statement.where.AbstractCondition;
import com.github.lit.jdbc.statement.where.WhereExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * User : liulu
 * Date : 2017/6/4 9:35
 * version $Id: UpdateImpl.java, v 0.1 Exp $
 */
public class UpdateImpl extends AbstractCondition<Update, WhereExpression<Update>> implements Update {


    private List<String> columns;

    private List<String> expressions;

    private WhereExpression<Update> whereExpression;


    public UpdateImpl(Class<?> clazz) {
        super(clazz);
        columns = new ArrayList<>();
        expressions = new ArrayList<>();
    }

    @Override
    public Update set(String fieldName, Object value) {

        columns.add(getColumnName(fieldName));

        expressions.add(value == null ? "null" : "?");
        if (value != null) {
            params.add(value);
        }

        return this;
    }


//    @Override
    public Update initEntity(Object entity, boolean isIgnoreNull) {
        Object key = BeanUtils.invokeReaderMethod(entity, tableInfo.getPkField());
        if (key == null) {
            throw new NullPointerException("primary key value must not be null for update!");
        }

        Map<String, String> fieldColumnMap = tableInfo.getFieldColumnMap();

        for (Map.Entry<String, String> entry : fieldColumnMap.entrySet()) {
            if (Objects.equals(tableInfo.getPkField(), entry.getKey())) {
                continue;
            }

            Object obj = BeanUtils.invokeReaderMethod(entity, entry.getKey());
            if (!isIgnoreNull || obj != null && !(obj instanceof String && ((String) obj).isEmpty())) {
                columns.add(entry.getValue());
                if (obj == null) {
                    expressions.add("null");
                } else {
                    expressions.add("?");
                    params.add(obj);
                }

            }
        }
        where(tableInfo.getPkField()).equalsTo(key);

        return this;
    }

    @Override
    public int execute() {
        return (int) executor.execute(new StatementContext(buildSql(), params, StatementContext.Type.UPDATE));
    }

    private String buildSql() {
        StringBuilder update = new StringBuilder("UPDATE ").append(table.getName()).append(" SET ");
        for (int i = 0; i < columns.size(); i++) {
            update.append(columns.get(i)).append(" = ").append(expressions.get(i)).append(", ");
        }
        update.deleteCharAt(update.lastIndexOf(",")).append("WHERE ").append(where);
        return update.toString();
    }

    @Override
    protected WhereExpression<Update> getExpression() {
        if (whereExpression == null) {
            whereExpression = new WhereExpression<>(this);
        }
        return whereExpression;
    }
}
