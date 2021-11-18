package com.zf1976.mayi.upms.biz.config;

import com.zf1976.mayi.common.core.util.RequestUtil;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author mac
 * @date 2021/4/8
 */
@Configuration
public class FeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            final Collection<String> tokenHeader = requestTemplate.headers().get(HttpHeaders.AUTHORIZATION);
            if (CollectionUtils.isEmpty(tokenHeader)) {
                String header = RequestUtil.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                requestTemplate.header("Authorization", header);
            }
        };
    }

}
