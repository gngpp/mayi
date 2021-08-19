package com.zf1976.mayi.common.security.support.signature.impl;

import com.google.common.base.Splitter;
import com.zf1976.mayi.common.core.util.Base64Util;
import com.zf1976.mayi.common.core.util.GuavaCacheUtil;
import com.zf1976.mayi.common.core.util.StringUtil;
import com.zf1976.mayi.common.encrypt.EncryptUtil;
import com.zf1976.mayi.common.security.support.signature.AbstractSignatureAuthenticationStrategy;
import com.zf1976.mayi.common.security.support.signature.SignatureState;
import com.zf1976.mayi.common.security.support.signature.StandardSignature;
import com.zf1976.mayi.common.security.support.signature.datasource.ClientDataSourceProvider;
import com.zf1976.mayi.common.security.support.signature.enums.SignaturePattern;
import com.zf1976.mayi.common.security.support.signature.exception.SignatureException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 隐秘签名认证策略
 *
 * @author mac
 * @date 2021/1/29
 **/
public class SecretSignatureAuthenticationStrategy extends AbstractSignatureAuthenticationStrategy {

    public SecretSignatureAuthenticationStrategy(ClientDataSourceProvider provider) {
        super(provider);
    }

    @Override
    public void onAuthenticate(HttpServletRequest request) {
        // 经过Base64编码签名内容
        String base64Content = request.getHeader(StandardSignature.SECRET_CONTENT);
        // 校验content
        if (StringUtil.isEmpty(base64Content)) {
            throw new SignatureException(SignatureState.NULL_PARAMS_VALUE);
        }
        // 解析
        Map<String, String> contentMap = this.validateAndParse(base64Content);
        // 获取时间戳
        Long timestamp = super.getTimestamp(contentMap);
        // 随机字符串
        String nonceStr = contentMap.get(StandardSignature.NONCE_STR);
        // 防重放校验
        super.validatePreventReplyAttack(nonceStr, timestamp);
        // 获取应用唯一标识
        String applyId = super.getApplyId(contentMap);
        // 构建签名
        String generateSignature = super.generateSignature(applyId, nonceStr, timestamp);
        // 获取原签名
        String rawSignature = super.getAndValidateSignature(contentMap);
        // 校验签名是否相同
        super.validateSignature(rawSignature, generateSignature);
        // 签名校验成功，缓存签名防止重放
        GuavaCacheUtil.setFixedOneMinutes(nonceStr, rawSignature);
    }

    @Override
    public boolean supports(SignaturePattern signatureModel) {
        return signatureModel != null;
    }


    /**
     * 解析
     *
     * @param base64Content content
     * @return key-value map
     */
    private Map<String, String> validateAndParse(String base64Content) {
        String result;
        try {
            String aesContent = Base64Util.decryptToString(base64Content);
            result = EncryptUtil.decryptForAesByCbc(aesContent);
        } catch (Exception e) {
            throw new SignatureException(SignatureState.PARAMETER_ANALYSIS_FAILURE, e.getMessage());
        }
        if (StringUtil.isEmpty(result)) {
            throw new SignatureException(SignatureState.NULL_PARAMS_VALUE);
        }
        Map<String, String> keyMap = new HashMap<>(4);
        Splitter.on("&")
                .trimResults()
                .omitEmptyStrings()
                .splitToList(result)
                .forEach(entry -> {
                    List<String> kv = Splitter.on("=")
                                              .splitToList(entry);
                    keyMap.put(kv.get(0), kv.get(1));
                });
        super.validateParameters(keyMap);
        return keyMap;
    }

}
