package com.github.lit.test.model;

import com.github.lit.jdbc.annotation.GeneratedValue;
import com.github.lit.jdbc.annotation.Id;
import com.github.lit.jdbc.annotation.Table;
import com.github.lit.jdbc.enums.GenerationType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * User : liulu
 * Date : 2017/6/11 21:00
 * version $Id: Supplier.java, v 0.1 Exp $
 */

@Data
@Builder
@Table(name = "test_supplier")
public class Supplier implements Serializable {

    private static final long serialVersionUID = 548793140920612818L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    private String code;
    private String name;
    private String address;
    private String contact;
    private String telephone;
    private String mobile;
}
