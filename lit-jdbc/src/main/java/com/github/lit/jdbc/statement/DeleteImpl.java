package com.github.lit.jdbc.statement;

import com.github.lit.commons.bean.BeanUtils;
import com.github.lit.jdbc.model.StatementContext;
import net.sf.jsqlparser.expression.HexValue;

/**
 * User : liulu
 * Date : 2017/6/4 9:51
 * version $Id: DeleteImpl.java, v 0.1 Exp $
 */
class DeleteImpl extends AbstractCondition<Delete> implements Delete {

    private net.sf.jsqlparser.statement.delete.Delete delete;


    DeleteImpl(Class<?> clazz) {
        super(clazz);
        delete = new net.sf.jsqlparser.statement.delete.Delete();
        delete.setTable(table);
    }

    @Override
    public DeleteImpl initEntity(Object entity) {
        if (entity == null) {
            throw new NullPointerException("entity is null, can not delete!");
        }
        Object key = BeanUtils.invokeReaderMethod(entity, tableInfo.getPkField());
        if (key != null && (!(key instanceof String) || !((String) key).isEmpty())) {
            idCondition(key);
        } else {
            throw new NullPointerException("entity [" + entity + "] id is null, can not delete!");
        }
        return this;
    }

    @Override
    public int execute() {
        delete.setWhere(new HexValue(where.toString()));
        return (int) executor.execute(new StatementContext(delete.toString(), params, StatementContext.Type.DELETE));
    }
}
