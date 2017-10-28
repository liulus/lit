package com.github.lit.commons.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * User : liulu
 * Date : 2017/3/21 19:34
 * version $Id: AppCheckedException.java, v 0.1 Exp $
 */
public class AppCheckedException extends RuntimeException {

    private static final long serialVersionUID = -679982092243426441L;

    @Getter
    @Setter
    private String errorCode;

    @Getter
    @Setter
    private String errorMsg;


    public AppCheckedException() {
        super();
    }

    public AppCheckedException(String message) {
        super(message);
        this.errorMsg = message;
    }

    public AppCheckedException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMsg = message;
    }

    public AppCheckedException(String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = message;
    }

    public AppCheckedException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMsg = message;
    }

    public AppCheckedException(Throwable cause) {
        super(cause);
    }

    protected AppCheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorMsg = message;
    }

    protected AppCheckedException(String errorCode, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
        this.errorMsg = message;
    }
}
