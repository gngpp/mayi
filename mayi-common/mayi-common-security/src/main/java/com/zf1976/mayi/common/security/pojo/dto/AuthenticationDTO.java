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
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.common.security.pojo.dto;


/**
 * @author mac
 * Create by Ant on 2020/9/1 下午11:10
 */
@Deprecated
public class AuthenticationDTO {

    /**
     * 登陆用户名
     */
    private String username;

    /**
     * 登陆密码 经过加密
     */
    private String password;

    /**
     * 验证码 uuid
     */
    private String uuid;

    /**
     * 验证码
     */
    private String code;

    public String getUsername() {
        return username;
    }

    public AuthenticationDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AuthenticationDTO setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public AuthenticationDTO setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getCode() {
        return code;
    }

    public AuthenticationDTO setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String toString() {
        return "AuthenticationDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", uuid='" + uuid + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
