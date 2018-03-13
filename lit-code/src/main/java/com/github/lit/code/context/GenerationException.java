package com.github.lit.code.context;

/**
 * User : liulu
 * Date : 2018/2/7 14:36
 * version $Id: GenerationException.java, v 0.1 Exp $
 */
public class GenerationException extends RuntimeException{

    private static final long serialVersionUID = 6061456844067018240L;

    public GenerationException() {
        super();
    }

    public GenerationException(String message) {
        super(message);
    }

    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerationException(Throwable cause) {
        super(cause);
    }

    protected GenerationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
