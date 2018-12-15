package com.github.lit.support.model;

import com.github.lit.support.common.Logic;
import com.github.lit.support.common.annotation.Condition;
import lombok.Data;

import java.util.List;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-15 11:08
 */
@Data
public class ProductCondition {

    private Long id;

    @Condition(logic = Logic.IN, property = "code")
    private List<String> codes;

    private Double price;

    @Condition(logic = Logic.LIKE)
    private String fullName;

    private String code;
}
