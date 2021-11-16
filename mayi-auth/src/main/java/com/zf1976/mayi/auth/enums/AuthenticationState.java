package com.zf1976.mayi.auth.enums;

/**
 * @author mac
 */

public enum AuthenticationState {

    // rsa 加密异常
    RSA_DECRYPT(401, "decrypt password is error"),

    // 认证异常
    BAD_CREDENTIALS(401, "the safe authentication parameter cannot be null"),

    // 非法jwt异常
    ILLEGAL_JWT(401, "illegal token"),

    // 验证吗异常
    CAPTCHA_FAIL(401, "captcha fail"),

    // 密码error异常
    USERNAME_PASSWORD_ERROR(401, "incorrect username or password"),

    // expired jwt 异常
    EXPIRED_JWT(401,"expired token"),

    // 未找到用户异常
    USER_NOT_FOUNT(401,"user not fount"),

    // 需要完整认真资源才能访问
    ILLEGAL_ACCESS(401,"full authentication is required to access this resource"),

    // 账号已禁用
    ACCOUNT_DISABLED(401, "Account has been disabled");


    private final int value;

    private final String reasonPhrase;

    AuthenticationState(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
