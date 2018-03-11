package com.github.lit.jdbc.statement;

import com.github.lit.commons.page.Page;
import com.github.lit.commons.page.PageList;
import com.github.lit.jdbc.enums.JoinType;
import com.github.lit.jdbc.enums.Logic;
import com.github.lit.jdbc.model.StatementContext;
import com.github.lit.jdbc.model.TableInfo;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.HexValue;
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
class SelectImpl<T> extends AbstractCondition<Select<T>> implements Select<T> {

    /**
     * 整体 Select 语句
     */
    private net.sf.jsqlparser.statement.select.Select select;

    private PlainSelect plainSelect;

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
     * 需要排除的字段
     */
    private List<String> exclude;

    private int addFieldLength = 0;

    private Class<T> entityClass;

    private Integer pageNum;

    private Integer pageSize;

    private boolean queryCount;

    private static List<SelectItem> COUNT_FUNC_ITEM;

    static {
        Function countFunc = new Function();
        countFunc.setName("count");
        countFunc.setAllColumns(true);

        SelectItem item = new SelectExpressionItem(countFunc);
        COUNT_FUNC_ITEM = Collections.singletonList(item);
    }

    SelectImpl(Class<T> clazz) {
        super(clazz);
        entityClass = clazz;
        initSelect();
    }

    private void initSelect() {
        select = new net.sf.jsqlparser.statement.select.Select();
        selectItems = new ArrayList<>(tableInfo.getFieldColumnMap().size());

        plainSelect = new PlainSelect();
        plainSelect.setSelectItems(selectItems);
        plainSelect.setFromItem(table);

        select.setSelectBody(plainSelect);
    }

    @Override
    public Select<T> include(String... fieldNames) {

        for (String fieldName : fieldNames) {
            selectItems.add(new SelectExpressionItem(buildColumn(fieldName)));
        }
        return this;
    }

    @Override
    public Select<T> exclude(String... fieldNames) {
        if (exclude == null) {
            exclude = new ArrayList<>(fieldNames.length);
        }
        for (String fieldName : fieldNames) {
            exclude.add(fieldName.trim());
        }
        return this;
    }

    @Override
    public Select<T> addField(Class<?> tableClass, String... fieldNames) {
        addFieldLength += fieldNames.length;
        initJoin();
        if (joinTableInfos.get(tableClass) == null) {
            TableInfo tableInfo = new TableInfo(tableClass);
            joinTableInfos.put(tableClass, tableInfo);
            joinTables.put(tableClass, new Table(tableInfo.getTableName()));
        }

        for (String fieldName : fieldNames) {
            selectItems.add(new SelectExpressionItem(buildColumn(tableClass, fieldName)));
        }
        return this;
    }

    @Override
    public Select<T> addFunc(String funcName) {
        addFunc(funcName, false);
        return this;
    }

    @Override
    public Select<T> addFunc(String funcName, String... fieldNames) {
        addFunc(funcName, false, fieldNames);
        return this;
    }

    @Override
    public Select<T> addFunc(String funcName, boolean distinct, String... fieldNames) {
        Function function = new Function();

        boolean noFuncColumns = fieldNames == null || fieldNames.length == 0 || fieldNames[0] == null;
        if (noFuncColumns) {
            function.setAllColumns(true);
        } else {
            List<Expression> funcColumns = new ArrayList<>(fieldNames.length);
            for (String fieldName : fieldNames) {
                funcColumns.add(buildColumn(fieldName));
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

    @Override
    public <E> Select<T> join(Class<E> tableClass) {
        return join(tableClass, false);
    }

    @Override
    public <E> Select<T> join(JoinType joinType, Class<E> tableClass) {
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
    public <E> Select<T> simpleJoin(Class<E> tableClass) {
        return join(tableClass, true);
    }

    private <E> Select<T> join(Class<E> tableClass, boolean simple) {
        initJoin();

        if (joinTableInfos.get(tableClass) == null) {
            TableInfo tableInfo = new TableInfo(tableClass);
            joinTableInfos.put(tableClass, tableInfo);
            joinTables.put(tableClass, new Table(tableInfo.getTableName()));
        }

        Join join = new Join();
        join.setRightItem(joinTables.get(tableClass));
        join.setSimple(simple);
        joins.add(join);

        return this;
    }

    private void initJoin() {
        if (joins == null) {
            joins = new ArrayList<>();
            joinTableInfos = new HashMap<>();
            joinTables = new HashMap<>();
        }
    }

    @Override
    public Select<T> on(Class<?> table1, String field1, Logic logic, Class<?> table2, String field2) {
        Join join = getLastJoin();
        String condition = buildColumn(table1, field1) + logic.getValue() + buildColumn(table2, field2);
        join.setOnExpression(new HexValue(condition));
        return this;
    }

    private Column buildColumn(Class<?> clazz, String field) {
        if (Objects.equals(clazz, entityClass)) {
            return buildColumn(field);
        }
        String column = joinTableInfos.get(clazz).getFieldColumnMap().get(field.trim());
        return column == null || column.isEmpty() ? new Column(joinTables.get(clazz), field) : new Column(joinTables.get(clazz), column);
    }

    private Join getLastJoin() {
        return joins.get(joins.size() - 1);
    }

    @Override
    public Select<T> joinCondition(Class<?> table1, String field1, Logic logic, Class<?> table2, String field2) {
        String condition = buildColumn(table1, field1) + logic.getValue() + buildColumn(table2, field2);
        where.append(condition);
        return this;
    }

    @Override
    public Select<T> and(Class<?> table, String field, Logic logic, Object... values) {
        String expression = getExpression(buildColumn(table, field), logic, values);
        and();
        where.append(expression);
        return this;
    }

    @Override
    public Select<T> or(Class<?> table, String field, Logic logic, Object... values) {
        String expression = getExpression(buildColumn(table, field), logic, values);
        or();
        where.append(expression);
        return this;
    }

    @Override
    public Select<T> groupBy(String... fields) {
        if (groupBy == null) {
            groupBy = new ArrayList<>(fields.length);
            having = new StringBuilder();
        }
        for (String field : fields) {
            groupBy.add(buildColumn(field));
        }
        return this;
    }

    @Override
    public Select<T> having(String fieldName, Object value) {
        return this.having(fieldName, Logic.EQ, value);
    }

    @Override
    public Select<T> having(String fieldName, Logic logic, Object... values) {
        String expression = getExpression(buildColumn(fieldName), logic, values);
        having.append(expression);
        return this;
    }

    @Override
    public Select<T> asc(String... fieldNames) {
        return order(true, fieldNames);
    }

    @Override
    public Select<T> desc(String... fieldNames) {
        return order(false, fieldNames);
    }

    private Select<T> order(boolean asc, String... fieldNames) {
        if (orderBy == null) {
            orderBy = new ArrayList<>();
        }

        for (String fieldName : fieldNames) {
            OrderByElement element = new OrderByElement();
            element.setExpression(buildColumn(fieldName));
            element.setAsc(asc);
            orderBy.add(element);
        }
        return this;
    }

    @Override
    public int count() {
        processSelect();
        plainSelect.setOrderByElements(null);
        plainSelect.setSelectItems(COUNT_FUNC_ITEM);
        int count = (int) executor.execute(new StatementContext(select.toString(), params, StatementContext.Type.SELECT_SINGLE, int.class));
        plainSelect.setOrderByElements(orderBy);
        plainSelect.setSelectItems(selectItems);
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
    public Select<T> page(Page pager) {
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

    private boolean processed;

    private void processSelect() {

        if (processed) {
            return;
        }

        if (selectItems.size() == addFieldLength) {

            Map<String, String> fieldColumnMap = tableInfo.getFieldColumnMap();
            if (exclude != null) {
                for (String field : exclude) {
                    fieldColumnMap.remove(field);
                }
            }
            for (String column : fieldColumnMap.values()) {
                selectItems.add(new SelectExpressionItem(new Column(table, column)));
            }
        }

        if (joins != null && joins.size() > 0) {
            plainSelect.setJoins(joins);
        }
        if (where != null && where.length() > 0) {
            plainSelect.setWhere(new HexValue(where.toString()));
        }
        if (groupBy != null && groupBy.size() > 0) {
            plainSelect.setGroupByColumnReferences(groupBy);
        }
        if (having != null && having.length() > 0) {
            plainSelect.setHaving(new HexValue(having.toString()));
        }
        if (orderBy != null && orderBy.size() > 0) {
            plainSelect.setOrderByElements(orderBy);
        }
        processed = true;
    }

}
