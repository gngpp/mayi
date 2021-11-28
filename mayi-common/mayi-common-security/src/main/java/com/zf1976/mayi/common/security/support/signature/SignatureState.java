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

package com.zf1976.mayi.common.security.support.signature;

/**
 * 接口签名state
 *
 * @author mac
 * @date 2021/1/29
 **/
public enum SignatureState {

    /**
     * 操作成功 {@code code=1}
     */
    SUCCESS(200, "操作成功"),

    /**
     * 操作失败，请稍候再试 {@code code=0}
     */
    FATAL(0, "操作失败，请稍候再试"),

    /**
     * 重放错误
     */
    ERROR_REPLY_ATTACK(401,"禁止重复请求"),

    /**
     * 参数解析失败 {@code code=403}
     */
    PARAMETER_ANALYSIS_FAILURE(401, "参数解析失败"),

    /**
     * 接口授权认证失败，签名不匹配 {@code code=403}
     */
    SIGNATURE_MISMATCH(401, "接口授权认证失败，签名不匹配"),

    /**
     * 参数不足 {@code code=403}
     */
    NULL_PARAMS_DATA(401, "参数不足"),

    /**
     * 错误参数
     */
    ERROR_PARAMS_DATA(401,"错误参数"),

    /**
     * 参数值为空 {@code code=403}
     */
    NULL_PARAMS_VALUE(401, "参数值为空"),

    /**
     * 缺少timestamp参数 {@code code=403}
     */
    MISSING_TIMESTAMP(401, "缺少timestamp参数"),

    /**
     * 缺少nonceStr参数 {@code code=403}
     */
    MISSING_NONCE_STR(401, "缺少nonceStr参数"),

    /**
     * 缺少appId参数 {@code code=403}
     */
    MISSING_APP_ID(401, "缺少appId参数"),

    /**
     * 缺少sign参数 {@code code=403}
     */
    MISSING_SIGN(401, "缺少sign参数"),

    /**
     * 服务器执行错误 {@code code=500}
     */
    ERROR(500, "服务器执行错误"),

    /**
     * 请求数据不存在，或数据集为空 {@code code=60000}
     */
    NULL_RESULT_DATA(60000, "请求数据不存在，或数据集为空");

    private final int value;

    private final String reasonPhrase;

    SignatureState(int value, String text) {
        this.value = value;
        this.reasonPhrase = text;
    }

    /**
     * 获取value
     *
     * @return int
     */
    public int getValue() {
        return value;
    }

    /**
     * 获取reasonPhrase
     *
     * @return String
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

}
