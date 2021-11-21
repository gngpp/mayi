/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

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

    /**
     * support binary
     *
     * @param value value
     * @return true/false
     */
    public boolean matchType(int value) {
        return ((value & this.value) == this.value);
    }

}
