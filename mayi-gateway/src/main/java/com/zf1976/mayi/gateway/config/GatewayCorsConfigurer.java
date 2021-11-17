package com.zf1976.mayi.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mac
 * @date 2021/3/25
 **/
@SuppressWarnings("all")
@Configuration
public class GatewayCorsConfigurer {

    @Bean
    public CorsWebFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        List<String> allowedOriginPatterns = new ArrayList<>();
        allowedOriginPatterns.add("*");
        config.setAllowedOriginPatterns(allowedOriginPatterns);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
