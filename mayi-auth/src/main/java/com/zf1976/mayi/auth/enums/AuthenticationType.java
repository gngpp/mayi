package com.zf1976.mayi.auth.enums;

/**
 * @author mac
 * @date 2021/2/5
 **/
public enum AuthenticationType {

    //===============================//
    //基础类型校验，每种类型用一个标志位记录//
    //===============================//

    /**
     * 0001
     */
    PHONE_WITH_VC(1,"手机号 + 短信验证码"),

    /**
     * 0010
     */
    PHONE_WITH_PASSWORD(1 << 1,"手机号 + 密码"),

    /**
     * 0100
     */
    EMAIL_WITH_VC(1 << 2,"邮箱 + 邮箱验证码"),

    /**
     * 1000
     */
    EMAIL_WITH_PASSWORD(1 << 3,"邮箱 + 密码"),

    /**
     * 10000
     */
    USERNAME_WITH_PASSWORD(1 << 4,"用户名 + 密码"),

    //==================================//
    //后台所支持等认证方式，由上面枚举值任意搭配//
    //==================================//

    /**
     * 0011
     */
    PHONE_WITH_VC_PASSWORD(PHONE_WITH_VC.value | PHONE_WITH_PASSWORD.value, "手机号 + 短信验证码/密码"),

    /**
     * 1100
     */
    EMAIL_WITH_VC_PASSWORD(EMAIL_WITH_VC.value | EMAIL_WITH_PASSWORD.value,"邮箱 + 邮箱验证码/密码"),

    /**
     * 11010
     */
    USERNAME_PHONE_OR_EMAIL__WITH_PASSWORD(PHONE_WITH_PASSWORD.value | EMAIL_WITH_PASSWORD.value,"用户名/手机/邮箱 + 密码"),

    /**
     * 11111
     */
    ALL(PHONE_WITH_PASSWORD.value | PHONE_WITH_VC.value | EMAIL_WITH_PASSWORD.value | EMAIL_WITH_VC_PASSWORD.value | USERNAME_PHONE_OR_EMAIL__WITH_PASSWORD.value | USERNAME_WITH_PASSWORD.value, "所有方式");


    private final int value;

    AuthenticationType(int value, String description) {
        this.value = value;
    }

    public boolean matchType(int value) {
        return ((value & this.value) == this.value);
    }

}
