package com.github.lit.support.model;

import com.github.lit.support.common.Logic;
import com.github.lit.support.common.annotation.Condition;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * User : liulu
 * Date : 2018/7/11 19:55
 * version $Id: GoodsQo.java, v 0.1 Exp $
 */
@Data
public class GoodsCondition implements Serializable {

    private static final long serialVersionUID = -1113673119261537637L;

    private Long id;

    @Condition(logic = Logic.IN, property = "code")
    private List<String> codes;

    private Double price;

    @Condition(logic = Logic.LIKE)
    private String fullName;

    private String code;

}
