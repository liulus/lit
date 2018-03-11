package com.github.lit.jdbc.statement.delete;

import com.github.lit.commons.bean.BeanUtils;
import com.github.lit.jdbc.model.StatementContext;
import com.github.lit.jdbc.statement.where.AbstractCondition;

/**
 * User : liulu
 * Date : 2017/6/4 9:51
 * version $Id: DeleteImpl.java, v 0.1 Exp $
 */
public class DeleteImpl extends AbstractCondition<Delete> implements Delete {

    private StringBuilder delete;


    public DeleteImpl(Class<?> clazz) {
        super(clazz);
        delete = new StringBuilder();
        delete.append("delete from ").append(table.getName());
    }

    @Override
    public DeleteImpl initEntity(Object entity) {
        Object keyVlue = BeanUtils.invokeReaderMethod(entity, tableInfo.getPkField());
        if (keyVlue != null && (!(keyVlue instanceof String) || !((String) keyVlue).isEmpty())) {
            this.where(tableInfo.getPkField()).equalsTo(keyVlue);
        } else {
            throw new NullPointerException("entity [" + entity + "] id is null, can not delete!");
        }
        return this;
    }

    @Override
    public int execute() {
        delete.append(where);
        return (int) executor.execute(new StatementContext(delete.toString(), params, StatementContext.Type.DELETE));
    }
}
