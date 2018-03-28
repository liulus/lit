package com.github.lit.code.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * User : liulu
 * Date : 2018/2/6 16:28
 * version $Id: JdbcConfig.java, v 0.1 Exp $
 */
@Getter
@Setter
@ToString
public class JdbcConfig implements Serializable{

    private static final long serialVersionUID = -488479893077026027L;

    private String dbName;

    private String driverClass;

    private String url;

    private String user;

    private String password;
}
