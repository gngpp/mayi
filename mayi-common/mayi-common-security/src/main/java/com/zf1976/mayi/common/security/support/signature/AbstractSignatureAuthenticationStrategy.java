package com.zf1976.mayi.common.security.support.signature;

import com.zf1976.mayi.common.core.util.CaffeineCacheUtil;
import com.zf1976.mayi.common.core.util.StringUtil;
import com.zf1976.mayi.common.encrypt.EncryptUtil;
import com.zf1976.mayi.common.security.support.signature.datasource.ClientDataSourceProvider;
import com.zf1976.mayi.common.security.support.signature.exception.SignatureException;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @date 2021/1/30
 **/
public abstract class AbstractSignatureAuthenticationStrategy implements SignatureAuthenticationStrategy {

    protected Map<String, SignatureState> stateMap = new HashMap<>(4);
    protected Long preventReplyAttackTime = 60000L;
    private final ClientDataSourceProvider clientDataSourceProvider;

    protected AbstractSignatureAuthenticationStrategy(ClientDataSourceProvider clientDataSourceProvider) {
        this.clientDataSourceProvider = clientDataSourceProvider;
        stateMap.put(StandardSignature.APPLY_ID, SignatureState.MISSING_APP_ID);
        stateMap.put(StandardSignature.TIMESTAMP, SignatureState.MISSING_TIMESTAMP);
        stateMap.put(StandardSignature.NONCE_STR, SignatureState.MISSING_NONCE_STR);
        stateMap.put(StandardSignature.SIGNATURE, SignatureState.MISSING_SIGN);
    }

    /**
     * 校验签名
     *
     * @param rawSignature 原签名
     * @param generateSignature 构建签名
     */
    protected void validateSignature(String rawSignature, String generateSignature) {
        if (!ObjectUtils.nullSafeEquals(generateSignature, rawSignature)) {
            throw new SignatureException(SignatureState.SIGNATURE_MISMATCH);
        }
    }

    /**
     * 获取并校验签名
     *
     * @param request 请求
     * @return 签名
     */
    protected String getAndValidateSignature(HttpServletRequest request) {
        String sign = request.getParameter(StandardSignature.SIGNATURE);
        if (StringUtil.isEmpty(sign)) {
            throw new SignatureException(SignatureState.MISSING_SIGN);
        }
        return sign;
    }

    /**
     * 从请求参数获取时间戳
     *
     * @param request 请求
     * @return timestamp
     */
    protected Long getTimestamp(HttpServletRequest request){
        Long timestamp;
        try {
            String timestampStr = request.getParameter(StandardSignature.TIMESTAMP);
            timestamp = NumberUtils.parseNumber(timestampStr, Long.class);
        } catch (Exception e) {
            throw new SignatureException(SignatureState.MISSING_TIMESTAMP, e.getMessage());
        }
        return timestamp;
    }

    /**
     * 从解析content获取时间戳
     *
     * @param contentMap content
     * @return timestamp
     */
    protected Long getTimestamp(Map<String, String> contentMap){
        Long timestamp;
        try {
            String timestampStr = contentMap.get(StandardSignature.TIMESTAMP);
            timestamp = NumberUtils.parseNumber(timestampStr, Long.class);
        } catch (Exception e) {
            throw new SignatureException(SignatureState.MISSING_TIMESTAMP, e.getMessage());
        }
        return timestamp;
    }

    /**
     * 从请求头获取客户端id
     *
     * @param request 请求
     * @return 客户端id
     */
    protected String getApplyId(HttpServletRequest request) {
        return request.getParameter(StandardSignature.APPLY_ID);
    }

    /**
     * 从解析content获取校验签名
     *
     * @param kv map
     * @return 签名
     */
    protected String getAndValidateSignature(Map<String, String> kv) {
        String sign = kv.get(StandardSignature.SIGNATURE);
        if (StringUtil.isEmpty(sign)) {
            throw new SignatureException(SignatureState.MISSING_SIGN);
        }
        return sign;
    }

    /**
     * 从解析content获取客户端id
     *
     * @param kv map
     * @return 客户端id
     */
    protected String getApplyId(Map<String, String> kv) {
        return kv.get(StandardSignature.APPLY_ID);
    }


    /**
     * 校验参数
     *
     * @param content content
     */
    protected void validateParameters(Map<String, String> content) {
        Assert.notNull(content,"content parameter cannot been null");
        for (Map.Entry<String, SignatureState> stateEntry : stateMap.entrySet()) {
            String parameter = content.get(stateEntry.getKey());
            if (StringUtil.isEmpty(parameter)) {
                throw new SignatureException(stateEntry.getValue());
            }
        }
    }

    /**
     * 验证参数是否缺少参数
     *
     * @param request request
     */
    protected void validateParameters(HttpServletRequest request) {
        Assert.notNull(request, "request cannot been null");
        for (Map.Entry<String, SignatureState> stateEntry : stateMap.entrySet()) {
            String parameter = request.getParameter(stateEntry.getKey());
            if (StringUtil.isEmpty(parameter)) {
                throw new SignatureException(stateEntry.getValue());
            }
        }
    }

    /**
     * 时间戳防重放校验
     *
     * @param timestamp 时间戳
     */
    protected void validatePreventReplyAttack(Long timestamp) {
        if (System.currentTimeMillis() - timestamp > preventReplyAttackTime) {
            throw new SignatureException(SignatureState.ERROR_REPLY_ATTACK);
        }
    }

    /**
     * 随机字符串防重放校验
     *
     * @param nonceStr 随机字符串
     */
    protected void validatePreventReplyAttack(String nonceStr) {
        Object rawSignature = CaffeineCacheUtil.getFixedOneMinutes(nonceStr);
        if (rawSignature != null) {
            throw new SignatureException(SignatureState.ERROR_REPLY_ATTACK);
        }
    }

    /**
     * 防重放校验
     *
     * @param nonceStr 随机字符串
     * @param timestamp 时间戳
     */
    protected void validatePreventReplyAttack(String nonceStr, Long timestamp) {
        this.validatePreventReplyAttack(timestamp);
        this.validatePreventReplyAttack(nonceStr);
    }



    /**
     * 构建签名
     *
     * @param nonceStr 随机字符串
     * @param timestamp 时间戳
     * @return /
     */
    protected String generateSignature(String applyId,String nonceStr, Long timestamp) {
        String applySecret = this.findApplySecret(applyId);
        List<String> params = new ArrayList<>();
        params.add(StandardSignature.APPLY_SECRET + "=" + applySecret);
        params.add(StandardSignature.APPLY_ID + "=" + applyId);
        params.add(StandardSignature.NONCE_STR + "=" + nonceStr);
        params.add(StandardSignature.TIMESTAMP + "=" + timestamp.toString());
        params.sort(String::compareTo);
        String signatureParams = String.join("&", params);
        return EncryptUtil.signatureByHmacSha1(signatureParams, applySecret);
    }

    private String findApplySecret(String applyId) {
        return this.clientDataSourceProvider.selectClientByClientId(applyId)
                                            .getClientSecret();
    }

}
