package com.zf1976.mayi.common.encrypt.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.mayi.common.core.util.Base64Util;
import com.zf1976.mayi.common.core.util.StringUtil;
import com.zf1976.mayi.common.encrypt.EncryptUtil;
import com.zf1976.mayi.common.encrypt.property.SecretProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * response 返回加密
 *
 * @author mac
 * @date 2021/1/3
 **/
@ControllerAdvice
public class ResponseBodyEncryptAdvice implements ResponseBodyAdvice<Object> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper jsonMapper;

    public ResponseBodyEncryptAdvice(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    /**
     * 是否支持 / 默认所有接口加密
     *
     * @param returnType 方法参数
     * @param aClass class
     * @return /
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
        // 全局开启加密 解密
//        if (SecretProperties.OPEN_ENCRYPT) {
//            // 注解优于配置
//            return returnType.getMethodAnnotation(Allow.class) == null;
//        } else {
//            return returnType.getMethodAnnotation(Encrypt.class) != null;
//        }
        return false;
    }

    /**
     * body 加密
     *
     * @param body body
     * @param methodParameter method parameter
     * @param type type
     * @param aClass convert clazz
     * @param serverHttpRequest server http request
     * @param serverHttpResponse server http response
     * @return /
     */
    @Override
    public Object beforeBodyWrite(@Nullable Object body,
                                  @NonNull MethodParameter methodParameter,
                                  @NonNull MediaType mediaType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> aClass,
                                  @NonNull ServerHttpRequest serverHttpRequest,
                                  @NonNull ServerHttpResponse serverHttpResponse) {
        if (body == null){
            return null;
        }
        try {
            String result = jsonMapper.writeValueAsString(body);
            String encryptResult = EncryptUtil.encryptForAesByCbc(result);
            if (SecretProperties.SHOW_LOG) {
                log.info("Pre-encrypted data：{}", result);
                log.info("After encryption data：{}", encryptResult);
            }
            return Base64Util.encryptToString(encryptResult);
        }catch (Exception e) {
            log.error("Encrypted data exception", e);
            return StringUtil.ENMPTY;
        }
    }

}
