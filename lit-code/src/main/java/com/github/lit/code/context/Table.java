package com.github.lit.code.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User : liulu
 * Date : 2018/2/7 18:51
 * version $Id: Table.java, v 0.1 Exp $
 */
@Getter
@Setter
@ToString
public class Table implements Serializable {

    private static final long serialVersionUID = -3416826548050707151L;

    private String catalog;

    private String schema;

    private String quoteString;

    private String name;

    private String primaryKey;

    private String desc;

    private List<Column> columns;

    private List<String> tasks;


    public String getFullTableName() {
        String result = " ";
        if (catalog != null && !catalog.isEmpty()) {
            result += catalog + ".";
        }

        if (schema != null && !schema.isEmpty()) {
            result += schema + ".";
        }

        return result + quoteString + name + quoteString + " ";


    }

    public Set<String> getImportClasses() {

        Set<String> importClasses = new HashSet<>();

        for (Column column : columns) {
            if (!column.getJavaClass().startsWith("java.lang")) {
                importClasses.add(column.getJavaClass());
            }
        }

        return importClasses;
    }
}
