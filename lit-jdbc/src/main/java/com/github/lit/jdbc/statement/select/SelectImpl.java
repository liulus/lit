package com.github.lit.jdbc.statement.select;

import com.github.lit.jdbc.enums.JoinType;
import com.github.lit.jdbc.enums.Logic;
import com.github.lit.jdbc.model.StatementContext;
import com.github.lit.jdbc.model.TableInfo;
import com.github.lit.jdbc.statement.where.AbstractCondition;
import com.github.lit.page.PageList;
import com.github.lit.page.PageParam;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

import java.util.*;

/**
 * User : liulu
 * Date : 2017/6/4 9:33
 * version $Id: SelectImpl.java, v 0.1 Exp $
 */
@SuppressWarnings("unchecked")
public class SelectImpl<T> extends AbstractCondition<Select<T>, SelectExpression<T>> implements Select<T> {

    /**
     * 整体 Select 语句
     */
    private PlainSelect select;

    /**
     * Select 语句的列，包括函数
     */
    private List<SelectItem> selectItems;

    /**
     * join 语句
     */
    private List<Join> joins;

    /**
     * joinTables 的所有 table 对象
     */
    private Map<Class<?>, Table> joinTables;

    /**
     * join 表的 表信息
     */
    private Map<Class<?>, TableInfo> joinTableInfos;

    /**
     * order by语句
     */
    private List<OrderByElement> orderBy;

    /**
     * 黑名单字段
     */
    private List<String> excludes;

    /**
     * groupBy 语句
     */
    private List<Expression> groupBy;

    /**
     * having 语句
     */
    private StringBuilder having;

    private SelectExpression<T> selectExpression;

    private Operation lastOperation;

    private boolean addDefaultColumn = true;

    private Class<T> entityClass;

    private Integer pageNum;

    private Integer pageSize;

    private boolean queryCount;

    private boolean processed;

    private static final List<SelectItem> COUNT_FUNC_ITEM;

    static {
        Function countFunc = new Function();
        countFunc.setName("count");
        countFunc.setAllColumns(true);

        SelectItem item = new SelectExpressionItem(countFunc);
        COUNT_FUNC_ITEM = Collections.singletonList(item);
    }

    public SelectImpl(Class<T> clazz) {
        super(clazz);
        entityClass = clazz;

        selectItems = new ArrayList<>(tableInfo.getFieldColumnMap().size());
        select = new PlainSelect();
        select.setSelectItems(selectItems);
        select.setFromItem(table);
    }

    // -------------------------------- select item ----------------------------------

    @Override
    public Select<T> include(String... properties) {
        // 自定义白名单, 不添加默认字段
        addDefaultColumn = false;
        for (String property : properties) {
            selectItems.add(new SelectExpressionItem(getColumnExpression(property)));
        }
        return this;
    }

    @Override
    public <R> Select<T> include(PropertyFunction<T, R> property) {
        return include(getProperty(property));
    }

    @Override
    public Select<T> exclude(String... properties) {
        if (excludes == null) {
            excludes = new ArrayList<>(properties.length);
        }
        excludes.addAll(Arrays.asList(properties));
        return this;
    }

    @Override
    public <R> Select<T> exclude(PropertyFunction<T, R> property) {
        return exclude(getProperty(property));
    }

    @Override
    public Select<T> additionalField(Class<?> tableClass, String... properties) {
        initJoin(tableClass);

        for (String property : properties) {
            selectItems.add(new SelectExpressionItem(getColumnExpression(tableClass, property)));
        }
        return this;
    }

    @Override
    public <S, R> Select<T> additionalField(PropertyFunction<S, R> property) {
        additionalField(getPropertyClass(property), getProperty(property));
        return this;
    }

    @Override
    public Select<T> function(String funcName) {
        function(funcName, false);
        return this;
    }

    @Override
    public Select<T> function(String funcName, String... properties) {
        function(funcName, false, properties);
        return this;
    }

    @Override
    public <S, R> Select<T> function(String funcName, PropertyFunction<S, R> property) {
        return function(funcName, getProperty(property));
    }

    @Override
    public Select<T> function(String funcName, boolean distinct, String... properties) {
        // 自定义函数时, 不添加默认字段
        addDefaultColumn = false;

        Function function = new Function();

        boolean noFuncColumns = properties == null || properties.length == 0 || properties[0] == null;
        if (noFuncColumns) {
            function.setAllColumns(true);
        } else {
            List<Expression> funcColumns = new ArrayList<>(properties.length);
            for (String property : properties) {
                funcColumns.add(getColumnExpression(property));
            }
            function.setParameters(new ExpressionList(funcColumns));
        }

        function.setName(funcName);
        function.setDistinct(distinct);

        selectItems.add(new SelectExpressionItem(function));
        return this;
    }


    @Override
    public Select<T> alias(String... alias) {
        int size = selectItems.size();
        int start = size - alias.length;
        for (int i = start; i < size; i++) {
            ((SelectExpressionItem) selectItems.get(i)).setAlias(new Alias(alias[i - start]));
        }
        return this;
    }

    @Override
    public Select<T> tableAlias(String alias) {
        if (joins == null || joins.size() == 0) {
            table.setAlias(new Alias(alias));
        } else {
            Join join = getLastJoin();
            join.getRightItem().setAlias(new Alias(alias));
        }
        return this;
    }

    // -------------------------------- join ----------------------------------

    @Override
    public Select<T> join(JoinType joinType, Class<?> tableClass) {
        join(tableClass, false);

        Join join = getLastJoin();
        switch (joinType) {
            case RIGHT:
                join.setRight(true);
                break;
            case LEFT:
                join.setLeft(true);
                break;
            case FULL:
                join.setFull(true);
                break;
            case NATURAL:
                join.setNatural(true);
                break;
            case CROSS:
                join.setCross(true);
                break;
            case INNER:
                join.setInner(true);
                break;
            case OUTER:
                join.setOuter(true);
                break;
            case SEMI:
                join.setSemi(true);
                break;
        }
        return this;
    }

    @Override
    public Select<T> join(Class<?> tableClass) {
        join(tableClass, false);
        return this;
    }

    @Override
    public Select<T> simpleJoin(Class<?> tableClass) {
        join(tableClass, true);
        return this;
    }

    @Override
    public SelectExpression<T> on(Class<?> tableClass, String property) {
        getLastJoin().setOnExpression(getColumnExpression(tableClass, property));
        return getExpression();
    }

    @Override
    public <S, R> SelectExpression<T> on(PropertyFunction<S, R> property) {
        getLastJoin().setOnExpression(getColumnExpression(getPropertyClass(property), getProperty(property)));
        return getExpression();
    }

    private void join(Class<?> tableClass, boolean simple) {
        lastOperation = Operation.JOIN;

        initJoin(tableClass);
        Join join = new Join();
        join.setRightItem(joinTables.get(tableClass));
        join.setSimple(simple);
        joins.add(join);
    }

    private void initJoin(Class<?> tableClass) {
        if (joins == null) {
            joins = new ArrayList<>();
            joinTableInfos = new HashMap<>();
            joinTables = new HashMap<>();
        }
        if (joinTableInfos.get(tableClass) == null) {
            TableInfo tableInfo = new TableInfo(tableClass);
            joinTableInfos.put(tableClass, tableInfo);
            joinTables.put(tableClass, new Table(tableInfo.getTableName()));
        }
    }

    private Join getLastJoin() {
        return joins.get(joins.size() - 1);
    }

    // -------------------------------- condition ----------------------------------

    @Override
    public SelectExpression<T> and(Class<?> tableClass, String property) {
        if (lastOperation == Operation.HAVING) {
            appendString(having, " AND ", tableClass, property);
            return getExpression();
        }
        appendString(where, " AND ", tableClass, property);
        return getExpression();
    }

    @Override
    public <S, R> SelectExpression<T> and(PropertyFunction<S, R> property) {
        return and(getPropertyClass(property), getProperty(property));
    }

    @Override
    public SelectExpression<T> or(Class<?> tableClass, String property) {
        if (lastOperation == Operation.HAVING) {
            appendString(having, " OR ", tableClass, property);
            return getExpression();
        }
        appendString(where, " OR ", tableClass, property);
        return getExpression();
    }

    @Override
    public <S, R> SelectExpression<T> or(PropertyFunction<S, R> property) {
        return or(getPropertyClass(property), getProperty(property));
    }

    @Override
    public Select<T> groupBy(String... fields) {
        lastOperation = Operation.HAVING;
        if (groupBy == null) {
            groupBy = new ArrayList<>(fields.length);
            having = new StringBuilder();
        }
        for (String field : fields) {
            groupBy.add(getColumnExpression(field));
        }
        return this;
    }

    @Override
    public <R> Select<T> groupBy(PropertyFunction<T, R> property) {
        return groupBy(getProperty(property));
    }

    @Override
    public SelectExpression<T> having(String property) {
        having.append(getColumnExpression(property).toString());
        return getExpression();
    }

    @Override
    public <R> SelectExpression<T> having(PropertyFunction<T, R> property) {
        return having(getProperty(property));
    }

    @Override
    public Select<T> asc(String... properties) {
        return order(true, properties);
    }

    @Override
    public <R> Select<T> asc(PropertyFunction<T, R> property) {
        return asc(getProperty(property));
    }

    @Override
    public Select<T> desc(String... properties) {
        return order(false, properties);
    }

    @Override
    public <R> Select<T> desc(PropertyFunction<T, R> property) {
        return desc(getProperty(property));
    }

    private Select<T> order(boolean asc, String... properties) {
        if (orderBy == null) {
            orderBy = new ArrayList<>();
        }

        for (String property : properties) {
            OrderByElement element = new OrderByElement();
            element.setExpression(getColumnExpression(property));
            element.setAsc(asc);
            orderBy.add(element);
        }
        return this;
    }

    @Override
    public int count() {
        processSelect();
        select.setOrderByElements(null);
        select.setSelectItems(COUNT_FUNC_ITEM);
        int count = (int) executor.execute(new StatementContext(select.toString(), params, StatementContext.Type.SELECT_SINGLE, int.class));
        select.setOrderByElements(orderBy);
        select.setSelectItems(selectItems);
        return count;
    }

    @Override
    public T single() {
        return single(entityClass);
    }

    @Override
    public <E> E single(Class<E> clazz) {
        processSelect();
        String sql;
        if (pageNum != null && pageSize != null) {
            sql = pageHandler.getPageSql(dbName, select.toString(), pageSize, pageNum);
        } else {
            sql = select.toString();
        }
        return (E) executor.execute(new StatementContext(entityClass, sql, params, StatementContext.Type.SELECT_SINGLE, clazz));
    }

    @Override
    public List<T> list() {
        return list(entityClass);
    }

    @Override
    public <E> List<E> list(Class<E> clazz) {

        if (pageNum != null && pageSize != null) {
            PageList<E> result;
            if (queryCount) {
                int totalRecord = count();
                result = new PageList<>(pageSize, pageNum, totalRecord);
                if (Objects.equals(totalRecord, 0)) {
                    return result;
                }
            } else {
                processSelect();
                result = new PageList<>(pageSize);
            }

            String pageSql = pageHandler.getPageSql(dbName, select.toString(), pageSize, pageNum);
            List execute = (List) executor.execute(new StatementContext(entityClass, pageSql, params, StatementContext.Type.SELECT_LIST, clazz));

            result.addAll(execute);

            return result;
        }

        processSelect();
        return (List<E>) executor.execute(new StatementContext(entityClass, select.toString(), params, StatementContext.Type.SELECT_LIST, clazz));
    }

    @Override
    public Select<T> page(PageParam pager) {
        return page(pager.getPageNum(), pager.getPageSize(), pager.isCount());
    }

    @Override
    public Select<T> page(int pageNum, int pageSize) {
        return page(pageNum, pageSize, true);
    }

    @Override
    public Select<T> page(int pageNum, int pageSize, boolean queryCont) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.queryCount = queryCont;
        return this;
    }

    @Override
    protected SelectExpression<T> getExpression() {
        if (selectExpression == null) {
            selectExpression = new SelectExpression<>(this);
        }
        return selectExpression;
    }

    @Override
    protected void appendString(String operator, String property) {
        if (lastOperation == Operation.JOIN) {
            throw new UnsupportedOperationException("join expression should not invoke this method!");
        }
        if (lastOperation == Operation.HAVING) {
            appendString(having, operator, entityClass, property);
            return;
        }
        appendString(where, operator, entityClass, property);
    }

    private void appendString(StringBuilder sb, String operator, Class<?> clazz, String property) {
        if (lastOperation == Operation.JOIN) {
            throw new UnsupportedOperationException("join expression should not invoke this method!");
        }
        if (sb.length() != 0) {
            sb.append(operator);
        }
        if (operator.contains("(")) {
            sb.append(operator);
        }
        if (property != null && property.length() > 0) {
            sb.append(getColumnExpression(clazz, property));
        }
    }

    @Override
    public void addParamValue(Logic logic, Object... values) {
        if (lastOperation == Operation.JOIN) {
            throw new UnsupportedOperationException("join on expression do not support this method!");
        }
        if (lastOperation == Operation.HAVING) {
            super.addParamValue(having, logic, values);
            return;
        }
        super.addParamValue(logic, values);
    }

    private void processSelect() {
        if (processed) {
            return;
        }
        if (addDefaultColumn) {
            Map<String, String> fieldColumnMap = tableInfo.getFieldColumnMap();
            if (excludes != null) {
                for (String field : excludes) {
                    fieldColumnMap.remove(field);
                }
            }
            for (String column : fieldColumnMap.values()) {
                selectItems.add(new SelectExpressionItem(new Column(table, column)));
            }
        }
        if (joins != null && joins.size() > 0) {
            select.setJoins(joins);
        }
        if (where != null && where.length() > 0) {
            select.setWhere(new HexValue(where.toString()));
        }
        if (groupBy != null && groupBy.size() > 0) {
            select.setGroupByColumnReferences(groupBy);
        }
        if (having != null && having.length() > 0) {
            select.setHaving(new HexValue(having.toString()));
        }
        if (orderBy != null && orderBy.size() > 0) {
            select.setOrderByElements(orderBy);
        }
        processed = true;
    }

    /**
     * 由 SelectExpression 调用, 可能是 join on, where, having 的条件, 根据 lastOperation 判断
     *
     * @param logic    logic
     * @param clazz    class
     * @param property property
     */
    public void setExpression(Logic logic, Class<?> clazz, String property) {

        if (lastOperation == Operation.JOIN) {
            lastOperation = Operation.NONE;
            setJoinOnExpression(logic, clazz, property);
            return;
        }
        Column columnExpression = getColumnExpression(clazz, property);
        if (lastOperation == Operation.HAVING) {
            having.append(logic.getCode()).append(columnExpression.toString());
            return;
        }
        where.append(logic.getCode()).append(columnExpression.toString());
    }

    private void setJoinOnExpression(Logic logic, Class<?> clazz, String property) {
        Join join = getLastJoin();
        Expression left = join.getOnExpression();

        BinaryExpression binaryExpression = null;
        switch (logic) {
            case EQ:
                binaryExpression = new EqualsTo();
                break;
            case NOT_EQ:
                binaryExpression = new EqualsTo();
                binaryExpression.setNot();
                break;
        }
        if (binaryExpression == null) {
            throw new UnsupportedOperationException("join on expression do not support this logic" + logic.getCode());
        }
        binaryExpression.setLeftExpression(left);
        binaryExpression.setRightExpression(getColumnExpression(clazz, property));
        join.setOnExpression(binaryExpression);
    }


    private Column getColumnExpression(String property) {
        String column = tableInfo.getFieldColumnMap().get(property);
        return column == null || column.isEmpty() ? new Column(property) : new Column(table, column);
    }

    private Column getColumnExpression(Class<?> clazz, String property) {
        if (clazz == null) {
            return new Column(property);
        }
        if (Objects.equals(clazz, entityClass)) {
            return getColumnExpression(property);
        }
        Table table = joinTables.get(clazz);
        if (table == null) {
            return new Column(property);
        }
        String column = joinTableInfos.get(clazz).getFieldColumnMap().get(property);
        return column == null || column.isEmpty() ? new Column(property) : new Column(table, column);
    }

    private String getColumnName(Class<?> clazz, String property) {
        if (clazz == null) {
            return property;
        }
        if (Objects.equals(clazz, entityClass)) {
            return tableInfo.getTableNameOrAlias() + "." + getColumn(property);
        }
        TableInfo joinTableInfo = joinTableInfos.get(clazz);
        if (joinTableInfo == null) {
            return property;
        }
        String column = joinTableInfo.getFieldColumnMap().get(property);
        return column == null || column.isEmpty() ? property : joinTableInfo.getTableNameOrAlias() + "." + column;
    }

    enum Operation {
        NONE,
        JOIN,
        WHERE,
        HAVING,;
    }

}
