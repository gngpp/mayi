package com.zf1976.mayi.auth.exception;

import com.zf1976.mayi.auth.enums.AuthenticationState;
import org.springframework.security.core.AuthenticationException;

/**
 * @author mac
 */
public class ExpiredJwtException extends AuthenticationException {

    private final int value;

    private final String reasonPhrase;

    public ExpiredJwtException(AuthenticationState state) {
        super(state.getReasonPhrase());
        this.value = state.getValue();
        this.reasonPhrase = state.getReasonPhrase();
    }

    public ExpiredJwtException(AuthenticationState state, Throwable throwable) {
        super(state.getReasonPhrase(), throwable);
        this.value = state.getValue();
        this.reasonPhrase = state.getReasonPhrase();
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
