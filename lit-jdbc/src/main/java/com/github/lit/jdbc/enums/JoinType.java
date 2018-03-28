package com.github.lit.jdbc.enums;

import com.github.lit.commons.enums.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User : liulu
 * Date : 2017/6/9 20:16
 * version $Id: JoinType.java, v 0.1 Exp $
 */
@Getter
@AllArgsConstructor
public enum JoinType implements IEnum {

    RIGHT("RIGHT", "RIGHT"),

    NATURAL("NATURAL", "NATURAL"),

    FULL("FULL", "FULL"),

    LEFT("LEFT", "LEFT"),

    CROSS("CROSS", "CROSS"),

    OUTER("OUTER", "OUTER"),

    INNER("INNER", "INNER"),

    SEMI("SEMI", "SEMI"),;

    private String code;

    private String desc;
}
