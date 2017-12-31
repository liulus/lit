package com.github.lit.jdbc.statement;

import com.github.lit.commons.bean.BeanUtils;
import com.github.lit.jdbc.model.StatementContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.schema.Column;

import java.util.*;

/**
 * User : liulu
 * Date : 2017/6/4 9:35
 * version $Id: UpdateImpl.java, v 0.1 Exp $
 */
class UpdateImpl extends AbstractCondition<Update> implements Update {

    protected static final Expression NULL_EXPR = new HexValue("null");

    private net.sf.jsqlparser.statement.update.Update update;

    private List<Column> columns;

    private List<Expression> values;

    UpdateImpl(Class<?> clazz) {
        super(clazz);
        update = new net.sf.jsqlparser.statement.update.Update();
        columns = new ArrayList<>();
        values = new ArrayList<>();
        update.setTables(Collections.singletonList(table));
        update.setColumns(columns);
        update.setExpressions(values);
    }

    @Override
    public Update set(String fieldName, Object value) {

        columns.add(buildColumn(fieldName));

        values.add(value == null ? NULL_EXPR : PARAM_EXPR);
        if (value != null) {
            params.add(value);
        }

        return this;
    }


    @Override
    public Update initEntity(Object entity, boolean isIgnoreNull) {
        if (entity == null) {
            return this;
        }
        Object key = BeanUtils.invokeReaderMethod(entity, tableInfo.getPkField());
        if (key == null) {
            throw new NullPointerException("entity [" + entity + "] id is null, can not update!");
        }

        Map<String, String> fieldColumnMap = tableInfo.getFieldColumnMap();

        for (Map.Entry<String, String> entry : fieldColumnMap.entrySet()) {
            if (Objects.equals(tableInfo.getPkField(), entry.getKey())) {
                continue;
            }

            Object obj = BeanUtils.invokeReaderMethod(entity, entry.getKey());
            if (!isIgnoreNull || obj != null && !(obj instanceof String && ((String) obj).isEmpty())) {
                columns.add(new Column(entry.getValue()));
                if (obj == null) {
                    values.add(NULL_EXPR);
                } else {
                    values.add(PARAM_EXPR);
                    params.add(obj);
                }

            }
        }
        idCondition(key);

        return this;
    }

    @Override
    public int execute() {
        update.setWhere(new HexValue(where.toString()));
        return (int) executor.execute(new StatementContext(update.toString(), params, StatementContext.Type.UPDATE));
    }
}
