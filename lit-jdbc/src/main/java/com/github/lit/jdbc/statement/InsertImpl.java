package com.github.lit.jdbc.statement;

import com.github.lit.commons.bean.BeanUtils;
import com.github.lit.commons.util.ClassUtils;
import com.github.lit.jdbc.enums.GenerationType;
import com.github.lit.jdbc.generator.EmptyKeyGenerator;
import com.github.lit.jdbc.generator.KeyGenerator;
import com.github.lit.jdbc.generator.SequenceGenerator;
import com.github.lit.jdbc.model.StatementContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User : liulu
 * Date : 2017/6/4 9:53
 * version $Id: InsertImpl.java, v 0.1 Exp $
 */
class InsertImpl extends AbstractStatement implements Insert {

    private net.sf.jsqlparser.statement.insert.Insert insert;

    private List<Column> columns = new ArrayList<>();

    private List<Expression> values = new ArrayList<>();

    private Object entity = null;


    InsertImpl(Class<?> clazz) {
        super(clazz);
        insert = new net.sf.jsqlparser.statement.insert.Insert();
        insert.setTable(table);
        insert.setColumns(columns);
        insert.setItemsList(new ExpressionList(values));
    }

    @Override
    public Insert into(String fieldName, Object value) {
        return into(fieldName, value, false);
    }

    @Override
    public Insert into(String fieldName, Object value, boolean isNative) {
        columns.add(buildColumn(fieldName));
        values.add(isNative ? new HexValue(value.toString()) : PARAM_EXPR);
        if (!isNative) {
            params.add(value);
        }
        return this;
    }

    @Override
    public Insert initEntity(Object entity) {
        if (entity == null) {
            return this;
        }
        this.entity = entity;
        Map<String, String> fieldColumnMap = tableInfo.getFieldColumnMap();

        for (Map.Entry<String, String> entry : fieldColumnMap.entrySet()) {
            Object obj = BeanUtils.invokeReaderMethod(entity, entry.getKey());
            if (obj != null && !(obj instanceof String && ((String) obj).isEmpty())) {
                columns.add(new Column(entry.getValue()));
                values.add(PARAM_EXPR);
                params.add(obj);
            }
        }
        return this;
    }

    @Override
    public Object execute() {

        StatementContext context = new StatementContext();

        KeyGenerator generator = getKeyGenerator();
        Serializable idValue = null;
        if (generator != null) {
            if (generator instanceof SequenceGenerator) {
                idValue = ((SequenceGenerator) generator).generateKey(dbName, tableInfo.getSequenceName());
                this.into(tableInfo.getPkField(), idValue, true);
            } else {
                idValue = generator.generateKey(dbName);
                this.into(tableInfo.getPkField(), idValue);
            }
        }

        context.setGenerateKeyByDb(tableInfo.isAutoGenerateKey() && (generator == null || generator.isGenerateBySql()));
        context.setPkColumn(tableInfo.getPkColumn());
        context.setSql(insert.toString());
        context.setParams(params);
        context.setStatementType(StatementContext.Type.INSERT);

        Object obj = executor.execute(context);
        Object id = context.isGenerateKeyByDb() ? obj : idValue;

        if (entity != null) {
            BeanUtils.invokeWriteMethod(entity, tableInfo.getPkField(), id);
            entity = null;
        }

        return id;
    }

    /**
     * 主键生成器实例的缓存
     */
    private static final Map<String, KeyGenerator> KEY_GENERATOR_CACHE = new ConcurrentHashMap<>();

    private static final SequenceGenerator SEQUENCE_GENERATOR = new SequenceGenerator();

    private KeyGenerator getKeyGenerator() {

        if (GenerationType.SEQUENCE == tableInfo.getGenerationType()) {
            return SEQUENCE_GENERATOR;
        }

        Class<? extends KeyGenerator> generatorClass = tableInfo.getGeneratorClass();
        if (generatorClass != null && generatorClass != EmptyKeyGenerator.class) {
            KeyGenerator keyGenerator = KEY_GENERATOR_CACHE.get(generatorClass.getName());
            if (keyGenerator == null) {
                keyGenerator = ClassUtils.newInstance(generatorClass);
                KEY_GENERATOR_CACHE.put(generatorClass.getName(), keyGenerator);
            }
            return keyGenerator;
        }

        return null;
    }

}
