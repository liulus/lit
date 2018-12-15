package com.github.lit.support.common.exception;

/**
 * User : liulu
 * Date : 2018-03-11 13:28
 * version $Id: BizException.java, v 0.1 Exp $
 */
public class BizException extends LitException {


    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String code, String message) {
        super(code, message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

}
