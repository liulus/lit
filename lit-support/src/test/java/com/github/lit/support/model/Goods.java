package com.github.lit.support.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * User : liulu
 * Date : 2018/7/11 19:23
 * version $Id: Goods.java, v 0.1 Exp $
 */
@Data
public class Goods implements Serializable {

    private static final long serialVersionUID = -6305173237589282633L;

    private Long id;

    private String code;

    private String fullName;

    private Double price;

    private Date createdAt;

}
