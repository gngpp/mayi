/*
 *
 *  * Copyright (c) 2021 gngpp
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

package com.gngpp.mayi.common.security.support.signature.impl;

import com.gngpp.mayi.common.core.util.GuavaCacheUtil;
import com.gngpp.mayi.common.security.support.signature.AbstractSignatureAuthenticationStrategy;
import com.gngpp.mayi.common.security.support.signature.StandardSignature;
import com.gngpp.mayi.common.security.support.signature.datasource.ClientDataSourceProvider;
import com.gngpp.mayi.common.security.support.signature.enums.SignaturePattern;

import javax.servlet.http.HttpServletRequest;

/**
 * 开放签名认证策略
 *
 * @author mac
 * @date 2021/1/29
 **/
public class OpenSignatureAuthenticationStrategy extends AbstractSignatureAuthenticationStrategy {

    public OpenSignatureAuthenticationStrategy(ClientDataSourceProvider provider) {
        super(provider);
    }

    @Override
    public void onAuthenticate(HttpServletRequest request) {
        // 验证是否缺少参数
        super.validateParameters(request);
        // 获取原签名
        String rawSignature = super.getAndValidateSignature(request);
        // 获取时间戳
        Long timestamp = super.getTimestamp(request);
        // 获取随机字符串
        String nonceStr = request.getParameter(StandardSignature.NONCE_STR);
        // 防重放校验
        super.validatePreventReplyAttack(nonceStr, timestamp);
        // 验证应用标识
        String applyId = super.getApplyId(request);
        // 构建签名
        String generateSignature = super.generateSignature(applyId, nonceStr, timestamp);
        // 校验签名是否相同
        super.validateSignature(rawSignature, generateSignature);
        // 签名校验成功，缓存签名防止重放
        GuavaCacheUtil.setFixedOneMinutes(nonceStr, rawSignature);
    }

    @Override
    public boolean supports(SignaturePattern signatureModel) {
        return signatureModel != null;
    }


}
