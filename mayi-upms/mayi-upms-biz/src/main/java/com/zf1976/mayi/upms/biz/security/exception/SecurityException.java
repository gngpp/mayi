package com.zf1976.mayi.upms.biz.security.exception;

/**
 * @author mac
 * @date 2021/5/12
 */
public class SecurityException extends RuntimeException {

    private final String message;

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public SecurityException(String message) {
        super(message);
        this.message = message;
    }

    public int getValue() {
        return 400;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
