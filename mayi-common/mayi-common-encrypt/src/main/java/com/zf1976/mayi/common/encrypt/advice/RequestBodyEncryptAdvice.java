package com.zf1976.mayi.common.encrypt.advice;

import com.zf1976.mayi.common.core.util.Base64Util;
import com.zf1976.mayi.common.core.util.StringUtil;
import com.zf1976.mayi.common.encrypt.EncryptUtil;
import com.zf1976.mayi.common.encrypt.property.SecretProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/1/3
 **/
@ControllerAdvice
public class RequestBodyEncryptAdvice implements RequestBodyAdvice {

    private static final Logger log = LoggerFactory.getLogger(RequestBodyEncryptAdvice.class);

    /**
     * 是否支持 / 取消拦截
     *
     * @param methodParameter method parameter
     * @param type type
     * @param aClass convert clazz
     * @return /
     */
    @Override
    public boolean supports(@NonNull MethodParameter methodParameter,
                            @NonNull Type type,
                            @NonNull Class<? extends HttpMessageConverter<?>> aClass) {

//        // 全局开启 解密
//        if (SecretProperties.OPEN_ENCRYPT) {
//            // 默认解密
//            return true;
//        } else {
//            return methodParameter.getMethodAnnotation(Decrypt.class) != null;
//        }
        return false;
    }

    /**
     * 操作前读取body
     *
     * @param httpInputMessage http input message
     * @param methodParameter method parameter
     * @param type type
     * @param aClass convert clazz
     * @return /
     * @throws IOException exception
     */
    @Override
    @NonNull
    public HttpInputMessage beforeBodyRead(@NonNull HttpInputMessage httpInputMessage,
                                           @NonNull MethodParameter methodParameter,
                                           @NonNull Type type,
                                           @NonNull Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return new CustomizerHttpInputMessage(httpInputMessage);
    }

    /**
     *
     * @param body body
     * @param httpInputMessage http input message
     * @param methodParameter method parameter
     * @param type type
     * @param aClass convert clazz
     * @return /
     */
    @Override
    @NonNull
    public Object afterBodyRead(@NonNull Object body,
                                @NonNull HttpInputMessage httpInputMessage,
                                @NonNull MethodParameter methodParameter,
                                @NonNull Type type,
                                @NonNull Class<? extends HttpMessageConverter<?>> aClass) {

        return body;
    }

    /**
     * 空body处理
     *
     * @param body body
     * @param httpInputMessage http input message
     * @param methodParameter method parameter
     * @param type type
     * @param aClass convert clazz
     * @return /
     */
    @Override
    public Object handleEmptyBody(@Nullable Object body,
                                  @NonNull HttpInputMessage httpInputMessage,
                                  @NonNull MethodParameter methodParameter,
                                  @NonNull Type type,
                                  @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
        return body;
    }

    private static class CustomizerHttpInputMessage implements HttpInputMessage {

        private final HttpHeaders headers;
        private final boolean showLog;
        private final HttpInputMessage httpInputMessage;
        private static final String ARRAY_START = "{";
        private static final String ARRAY_END = "}";

        public CustomizerHttpInputMessage(HttpInputMessage httpInputMessage) {
            this.showLog = SecretProperties.SHOW_LOG;
            this.headers = httpInputMessage.getHeaders();
            this.httpInputMessage = httpInputMessage;
        }

        @Override
        @NonNull
        public InputStream getBody() throws IOException {
            InputStreamReader inputStreamReader = new InputStreamReader(this.httpInputMessage.getBody());
            String base64Content = new BufferedReader(inputStreamReader).lines()
                                                                        .collect(Collectors.joining(System.lineSeparator()));
            String decryptBody = Base64Util.decryptToString(base64Content);
            if (base64Content.startsWith(ARRAY_START) && base64Content.lastIndexOf(ARRAY_END) > 0) {
                if (this.showLog) {
                    log.info("Unencrypted without decryption:{}", decryptBody);
                }
            } else {
                StringBuilder jsonBuilder = new StringBuilder();
                String rawBody = decryptBody;
                decryptBody = decryptBody.replaceAll(" ", "+");
                if (!StringUtil.isEmpty(decryptBody)) {
                    String[] split = decryptBody.split("\\|");
                    for (String key : split) {
                        String value = EncryptUtil.decryptForAesByCbc(key);
                        jsonBuilder.append(value);
                    }
                }
                decryptBody = jsonBuilder.toString();
                if (this.showLog) {
                    log.info("Encrypted data received：{},After decryption：{}", rawBody, decryptBody);
                }
            }
            return new ByteArrayInputStream(decryptBody.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        @NonNull
        public HttpHeaders getHeaders() {
            return this.headers;
        }
    }
}
