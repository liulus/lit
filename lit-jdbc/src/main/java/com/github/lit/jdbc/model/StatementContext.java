package com.github.lit.jdbc.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User : liulu
 * Date : 2017/6/4 10:21
 * version $Id: StatementContext.java, v 0.1 Exp $
 */
@Data
@NoArgsConstructor
public class StatementContext {

    /**
     * 主键信息
     */
    private String pkColumn;

    /**
     * 主键是否数据库生成
     */
    private boolean generateKeyByDb;

    /**
     * 语句操作类型
     */
    private Type statementType;

    /**
     * sql语句
     */
    private String sql;

    /**
     * 语句对应的参数
     */
    private List<Object> params;

    /**
     * 主表对应的实体类
     */
    private Class<?> entityClass;

    /**
     * 返回的类型
     */
    private Class<?> requireType;

    public StatementContext(String sql, List<Object> params, Type type) {
        this.sql = sql;
        this.params = params;
        this.statementType = type;
    }

    public StatementContext(String sql, List<Object> params, Type type, Class<?> requireType) {
        this.sql = sql;
        this.params = params;
        this.statementType = type;
        this.requireType = requireType;
    }

    public StatementContext(Class<?> entityClass, String sql, List<Object> params, Type type, Class<?> requireType) {
        this.entityClass = entityClass;
        this.sql = sql;
        this.params = params;
        this.statementType = type;
        this.requireType = requireType;
    }


    public enum Type {
        INSERT,
        DELETE,
        UPDATE,
        SELECT_SINGLE,
        SELECT_LIST,
    }

}
