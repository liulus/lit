package com.lit.support.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * User : liulu
 * Date : 2018-03-11 13:37
 * version $Id: LitException.java, v 0.1 Exp $
 */
class LitException extends RuntimeException {

    private static final long serialVersionUID = -7052223373308595812L;

    @Getter
    @Setter
    private String code;

    LitException() {
        super();
    }

    LitException(ExceptionDefinition exceptionDefinition) {
        super(exceptionDefinition.getMessage());
        this.code = exceptionDefinition.getCode();
    }

    LitException(String message) {
        super(message);
    }

    LitException(String code, String message) {
        super(message);
        this.code = code;
    }

    LitException(String message, Throwable cause) {
        super(message, cause);
    }

    LitException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    LitException(Throwable cause) {
        super(cause);
    }


}
