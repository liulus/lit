package com.github.lit.code.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * User : liulu
 * Date : 2018/2/8 11:34
 * version $Id: Column.java, v 0.1 Exp $
 */
@Getter
@Setter
@ToString
public class Column implements Serializable {

    private static final long serialVersionUID = 4076352228046456676L;

    private String name;

    private Integer displaySize;

    private Integer scale;

    private String comment;

    private Boolean primaryKey;

    private Boolean autoIncrement;

    private String jdbcType;

    private String javaType;

    private String javaClass;

}
