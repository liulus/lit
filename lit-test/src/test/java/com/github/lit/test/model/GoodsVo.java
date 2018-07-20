package com.github.lit.test.model;

import com.github.lit.page.Page;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * User : liulu
 * Date : 2017-3-6 22:15
 * version $Id: GoodsVo.java, v 0.1 Exp $
 */
@Data
@Builder
public class GoodsVo extends Page implements Serializable {

    private static final long serialVersionUID = -6502691308871630951L;

    private Long goodsId;

    private String code;

    private String barCode;

    private String specification;

    private String unit;

    private String category;

    private String categoryName;

    private String purchaserCode;

    private String supplierCode;

    private String supplierName;

    private String supplierAddr;

    private String name;

    private Double price;

    private Integer inventory;

    private Date gmtCreate;

    private Double startPrice;

    private Double endPrice;




}
