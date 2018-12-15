package com.github.lit.jdbc.statement.insert;

import com.github.lit.jdbc.enums.GenerationType;
import com.github.lit.jdbc.generator.EmptyKeyGenerator;
import com.github.lit.jdbc.generator.KeyGenerator;
import com.github.lit.jdbc.generator.SequenceGenerator;
import com.github.lit.jdbc.model.StatementContext;
import com.github.lit.jdbc.statement.AbstractStatement;
import com.github.lit.support.util.ClassUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
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
public class InsertImpl extends AbstractStatement implements Insert {

    private List<String> columns = new ArrayList<>();

    private List<String> expressions = new ArrayList<>();

    private Object entity = null;


    public InsertImpl(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public Insert set(String property, Object value) {

        columns.add(getColumn(property));
        if (value == null) {
            expressions.add("null");
            return this;
        }
        expressions.add(isNative ? value.toString() : "?");
        if (isNative) {
            isNative = false;
        } else {
            params.add(value);
        }
        return this;
    }

    @Override
    public <T, R> Insert set(PropertyFunction<T, R>  property, Object value) {
        return set(getProperty(property), value);
    }

    @Override
    public Insert natively() {
        isNative = true;
        return this;
    }

    //    @Override
    public Insert initEntity(Object entity) {
        this.entity = entity;
        Map<String, String> fieldColumnMap = tableInfo.getFieldColumnMap();

        for (Map.Entry<String, String> entry : fieldColumnMap.entrySet()) {
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(entity.getClass(), entry.getKey());
            Object value = ReflectionUtils.invokeMethod(pd.getReadMethod(), entity);
            if (value != null) {
                columns.add(entry.getValue());
                expressions.add("?");
                params.add(value);
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
                this.natively().set(tableInfo.getPkProperty(), idValue);
            } else {
                idValue = generator.generateKey(dbName);
                this.set(tableInfo.getPkProperty(), idValue);
            }
        }

        context.setStatementType(StatementContext.Type.INSERT);
        context.setGenerateKeyByDb(tableInfo.isAutoGenerateKey() && (generator == null || generator.isGenerateBySql()));
        context.setPkColumn(tableInfo.getPkColumn());
        context.setSql(buildSql());
        context.setParams(params);


        Object obj = executor.execute(context);
        Object id = context.isGenerateKeyByDb() ? obj : idValue;

        if (entity != null) {
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(entity.getClass(), tableInfo.getPkProperty());
            ReflectionUtils.invokeMethod(pd.getWriteMethod(), entity, id);
        }

        return id;
    }

    /**
     * 构建 insert sql
     *
     * @return insert sql
     */
    private String buildSql() {
        if (columns.size() <= 0) {
            return "";
        }
        StringBuilder insertBuilder = new StringBuilder("INSERT INTO ").append(tableInfo.getTableName()).append(" ( ");
        StringBuilder valueBuilder = new StringBuilder(") VALUES ( ");

        for (int i = 0; i < columns.size(); i++) {
            insertBuilder.append(columns.get(i)).append(", ");
            valueBuilder.append(expressions.get(i)).append(", ");
        }
        // 去掉最后一个逗号
        insertBuilder.deleteCharAt(insertBuilder.lastIndexOf(","));
        valueBuilder.deleteCharAt(valueBuilder.lastIndexOf(",")).append(")");

        return insertBuilder.append(valueBuilder).toString();
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
