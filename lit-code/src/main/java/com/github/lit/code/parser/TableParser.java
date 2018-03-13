package com.github.lit.code.parser;

import com.github.lit.code.config.Configuration;
import com.github.lit.code.config.JdbcConfig;
import com.github.lit.code.context.Column;
import com.github.lit.code.context.ConfigConst;
import com.github.lit.code.context.GenerationException;
import com.github.lit.code.context.Table;
import com.github.lit.code.datebase.DataBaseProvider;
import com.github.lit.code.datebase.DateBaseProviderFactory;
import com.github.lit.code.util.BeanUtils;
import com.github.lit.code.util.DBUtils;
import com.oracle.javafx.jmx.json.JSONDocument;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * User : liulu
 * Date : 2018/2/7 18:58
 * version $Id: TableParser.java, v 0.1 Exp $
 */
public class TableParser implements ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(TableParser.class.getName());

    @Override
    public String getConfigKey() {
        return ConfigConst.TABLE;
    }

    @Override
    public void parser(Configuration configuration, JSONDocument jsonDocument) {

        JdbcConfig jdbcConfig = configuration.getJdbcConfig();
        if (jdbcConfig == null) {
            throw new GenerationException("未找到数据库连接配置, 不能解析表信息! 如果不需要解析表信息, 请将配置文件中的 table 配置删除!");
        }
        if (jsonDocument.isArray()) {
            throw new GenerationException("table 配置项不能是数组!");
        }
        Table table = BeanUtils.mapToBean(jsonDocument.object(), Table.class);
        configuration.setTable(table);

        // 初始化数据连接
        DBUtils.createConnection(configuration.getJdbcConfig());
        initTableMetaData(table);

        // 转换自定义 jdbcType 和 javaType 的映射
        for (Column column : table.getColumns()) {
            String javaClass = configuration.getConverter(column.getJdbcType());
            if (javaClass != null && !javaClass.isEmpty()) {
                column.setJavaClass(javaClass);
            }
            column.setJavaType(column.getJavaClass().substring(column.getJavaClass().lastIndexOf(".") + 1));
        }
    }

    /**
     * @param table
     */
    private void initTableMetaData(Table table) {

        Connection connection = DBUtils.getConnection();
        try {
            if (table.getCatalog() == null || table.getCatalog().isEmpty()) {
                table.setCatalog(connection.getCatalog());
            }
            if (table.getSchema() == null || table.getSchema().isEmpty()) {
                table.setSchema(connection.getSchema());
            }
            if (table.getQuoteString() == null || table.getQuoteString().isEmpty()) {
                table.setQuoteString(connection.getMetaData().getIdentifierQuoteString());
            }
            String dbName = connection.getMetaData().getDatabaseProductName();
            DataBaseProvider provider = DateBaseProviderFactory.getDataBaseProvider(dbName);
            Map<String, String> columnCommentMap = null;
            if (provider != null) {
                columnCommentMap = provider.getColumnComment(connection, table);
            } else {
                LOGGER.warning("未能找到 " + dbName + " 数据库查询列备注信息的provider!");
            }

            ResultSet primaryKeys = connection.getMetaData().getPrimaryKeys(table.getCatalog(), table.getSchema(), table.getName());
            while (primaryKeys.next()) {
                table.setPrimaryKey(primaryKeys.getString("COLUMN_NAME").toLowerCase());
            }

            String querySql = "select * from" + table.getFullTableName() + "where 1=2";
            LOGGER.info("查询表元数据, 执行sql: " + querySql);
            PreparedStatement ps = connection.prepareStatement(querySql);
            ResultSetMetaData tableMetaData = ps.executeQuery().getMetaData();

            List<Column> columns = new ArrayList<>(tableMetaData.getColumnCount());
            for (int i = 0; i < tableMetaData.getColumnCount(); i++) {
                Column column = new Column();
                column.setName(tableMetaData.getColumnName(i + 1).toLowerCase()); // 列名 - 小写
                column.setJdbcType(tableMetaData.getColumnTypeName(i + 1)); // jdbcType eg: VARCHAR
                column.setJavaClass(tableMetaData.getColumnClassName(i + 1)); // jdbcType 对应的 javaClass eg: java.lang.String
                column.setDisplaySize(tableMetaData.getColumnDisplaySize(i + 1));
                column.setScale(tableMetaData.getScale(i + 1));
                column.setAutoIncrement(tableMetaData.isAutoIncrement(i + 1)); //是否是自增列
                // 列备注
                if (columnCommentMap != null) {
                    String comment = columnCommentMap.get(column.getName());
                    if (comment != null && !comment.isEmpty()) {
                        column.setComment(comment);
                    }
                }
                // 列是否主键
                if (table.getPrimaryKey() == null || table.getPrimaryKey().isEmpty()) {
                    column.setPrimaryKey(false);
                } else {
                    column.setPrimaryKey(Objects.equals(column.getName(), table.getPrimaryKey()));
                }

                columns.add(column);
            }
            table.setColumns(columns);
        } catch (Exception e) {
            throw new GenerationException("初始化表元数据信息异常", e);
        } finally {
            DBUtils.closeConnection();
        }
    }
}
