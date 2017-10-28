package com.github.lit.jdbc.enums;

import com.github.lit.commons.util.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User : liulu
 * Date : 2017/3/18 13:19
 * version $Id: Logic.java, v 0.1 Exp $
 */
@Getter
@AllArgsConstructor
public enum Logic implements IEnum {

    EQ("equal", " = "),

    NOT_EQ("not equal", " != "),

    LT("less than", " < "),

    GT("grater than", " > "),

    LTEQ("less than or equal", " <= "),

    GTEQ("grater than or equal", " >= "),

    LIKE("like", " like "),

    NOT_LIKE("not like", " not like "),

    IN("in", " in "),

    NOT_IN("not in", " not in "),

    NULL("is null", " is null "),

    NOT_NULL("is not null", " is not null "),
    ;


    private String text;

    private String value;

}
