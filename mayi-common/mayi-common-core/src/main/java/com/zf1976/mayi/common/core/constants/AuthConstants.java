/*
 *
 *  * Copyright (c) 2021 zf1976
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

package com.zf1976.mayi.common.core.constants;

/**
 * @author ant
 * Create by Ant on 2021/2/16 12:54 PM
 */
public interface AuthConstants {

    /**
     * 验证码前缀
     */
    String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY:";

    /**
     * 用户信息缓存
     */
    String DETAILS = "details";

    /**
     * 用户id
     */
    String JWT_USER_ID_KEY = "id";

    /**
     * 客户端id
     */
    String JWT_CLIENT_ID_KEY = "client_id";

    /**
     * JWT存储权限属性
     */
    String JWT_AUTHORITIES_KEY = "authorities";

    /**
     * 空值权限
     */
    String EMPTY_AUTHORITIES_VALUE = "";

    /**
     * grant type
     */
    String GRANT_TYPE = "grant_type";

    /**
     * 资源所有者
     */
    String OWNER = "ROLE_root";

    /**
     * 签发方
     */
    String ISSUER = "issuer";


    /**
     * jwt 唯一标示
     */
    String JTI = "jti";

    /**
     * 签发时间
     */
    String IAT = "iat";

    /**
     * 用户名
     */
    String USERNAME = "username";

    /**
     * 密码
     */
    String PASSWORD = "password";

}
