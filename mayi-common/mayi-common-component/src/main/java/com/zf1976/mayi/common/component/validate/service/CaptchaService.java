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

package com.zf1976.mayi.common.component.validate.service;

/**
 * @author mac
 * Create by Ant on 2020/6/18 5:37 下午
 */
public interface CaptchaService {

    /**
     * 发送验证码
     *
     * @param key      唯一标识
     * @param value    需要发送的验证码
     * @param expire   有效时间
     * @param timeUnit 时间单位
     * @return 是否发送成功
     */
    boolean storeCaptcha(String key, String value);

    /**
     * 校验验证码
     *
     * @param prefixAndKey 前缀加key
     * @param code         需要校验的验证码
     * @return 是否正确
     */
    boolean validateCaptcha(String prefixAndKey, String code);

    /**
     * 清理验证码
     *
     * @param prefixAndKey 前缀加key
     */
    void clearCaptcha(String prefixAndKey);

}
