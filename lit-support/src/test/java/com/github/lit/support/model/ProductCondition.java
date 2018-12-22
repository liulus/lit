package com.github.lit.support.model;

import com.github.lit.support.annotation.Condition;
import com.github.lit.support.page.PageRequest;
import com.github.lit.support.sql.Logic;
import lombok.Data;

import java.util.List;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-15 11:08
 */
@Data
public class ProductCondition extends PageRequest {

    private static final long serialVersionUID = 6026171247313182830L;

    private Long id;

    @Condition(logic = Logic.IN, property = "code")
    private List<String> codes;

    private Double price;

    @Condition(logic = Logic.LIKE)
    private String fullName;

    private String code;
}
