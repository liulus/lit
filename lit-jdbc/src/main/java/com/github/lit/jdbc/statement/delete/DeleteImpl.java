package com.github.lit.jdbc.statement.delete;

import com.github.lit.commons.bean.BeanUtils;
import com.github.lit.jdbc.model.StatementContext;
import com.github.lit.jdbc.statement.where.AbstractCondition;
import com.github.lit.jdbc.statement.where.WhereExpression;

/**
 * User : liulu
 * Date : 2017/6/4 9:51
 * version $Id: DeleteImpl.java, v 0.1 Exp $
 */
public class DeleteImpl extends AbstractCondition<Delete, WhereExpression<Delete>> implements Delete {

    private WhereExpression<Delete> whereExpression;


    public DeleteImpl(Class<?> clazz) {
        super(clazz);
    }

//    @Override
    public DeleteImpl initEntity(Object entity) {

        Object keyValue = BeanUtils.invokeReaderMethod(entity, tableInfo.getPkField());
        if (keyValue != null && (!(keyValue instanceof String) || !((String) keyValue).isEmpty())) {
            this.where(tableInfo.getPkField()).equalsTo(keyValue);
        } else {
            throw new NullPointerException("primary key value must not be null for delete!");
        }
        return this;
    }

    @Override
    public int execute() {
        String sql = "DELETE FROM " + table.getName() + " WHERE " + where;
        return (int) executor.execute(new StatementContext(sql, params, StatementContext.Type.DELETE));
    }

    @Override
    protected WhereExpression<Delete> getExpression() {
        if (whereExpression == null) {
            whereExpression = new WhereExpression<>(this);
        }
        return whereExpression;
    }
}
