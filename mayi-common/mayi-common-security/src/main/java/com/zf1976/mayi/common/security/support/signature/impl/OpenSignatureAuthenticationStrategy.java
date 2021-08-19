package com.zf1976.mayi.common.security.support.signature.impl;

import com.zf1976.mayi.common.core.util.GuavaCacheUtil;
import com.zf1976.mayi.common.security.support.signature.AbstractSignatureAuthenticationStrategy;
import com.zf1976.mayi.common.security.support.signature.StandardSignature;
import com.zf1976.mayi.common.security.support.signature.datasource.ClientDataSourceProvider;
import com.zf1976.mayi.common.security.support.signature.enums.SignaturePattern;

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
