package com.hackyle.blog.business.common.exception;

/**
 * 在GlobalExceptionHandler中有拦截本自定义运行时异常
 */
public class TokenException extends RuntimeException {
    public TokenException() {
        super();
    }

    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
