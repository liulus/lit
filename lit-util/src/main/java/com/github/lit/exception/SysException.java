package com.github.lit.exception;

/**
 * User : liulu
 * Date : 2018-03-11 13:36
 * version $Id: SysException.java, v 0.1 Exp $
 */
public class SysException extends LitException  {

    public SysException() {
        super();
    }

    public SysException(String message) {
        super(message);
    }

    public SysException(String code, String message) {
        super(code, message);
    }

    public SysException(String message, Throwable cause) {
        super(message, cause);
    }

    public SysException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public SysException(Throwable cause) {
        super(cause);
    }

}
