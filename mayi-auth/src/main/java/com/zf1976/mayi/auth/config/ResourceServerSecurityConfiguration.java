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

package com.zf1976.mayi.auth.config;

import com.zf1976.mayi.auth.codec.JwtDecoderEnhancer;
import com.zf1976.mayi.auth.codec.Md5PasswordEncoder;
import com.zf1976.mayi.auth.filter.handler.access.Oauth2AccessDeniedHandler;
import com.zf1976.mayi.auth.filter.handler.access.Oauth2AuthenticationEntryPoint;
import com.zf1976.mayi.auth.oauth2.authorization.CustomizeJwtGrantedAuthoritiesConverter;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;


/**
 * @author mac
 * Create by Ant on 2020/9/1 上午11:32
 */
@Configuration
@EnableWebSecurity
public class ResourceServerSecurityConfiguration {

    private final SecurityProperties properties;

    private final PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

    private volatile KeyPair keyPair;

    public ResourceServerSecurityConfiguration(SecurityProperties securityProperties) {
        this.properties = securityProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }

    /**
     * authentication server key pair
     *
     * @return {@link KeyPair}
     */
    @Bean
    @DependsOn(value = "securityProperties")
    @ConditionalOnMissingBean
    public KeyPair keyPair() {
        if (this.keyPair == null) {
            synchronized (this) {
                if (this.keyPair == null) {
                    ClassPathResource classPathResource = new ClassPathResource("root.jks");
                    KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, properties.getRsaSecret().toCharArray());
                    this.keyPair = keyStoreKeyFactory.getKeyPair("root", properties.getRsaSecret().toCharArray());
                }
            }
        }
        return this.keyPair;
    }

    /**
     * resource server configuration
     *
     * @param http http security
     * @return {@link SecurityFilterChain}
     * @throws Exception e
     */
    @Bean
    @Order(2)
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http, OAuth2AuthorizationService authorizationService) throws Exception {

        // authorization processing
        http.authorizeHttpRequests((authorize) -> authorize
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // compatible with old version interface
                    .antMatchers("/oauth2/token_key", "/oauth2/code", "/oauth2/security/revoke/**").permitAll()
                    .antMatchers(properties.getIgnoreUri()).permitAll()
                    .anyRequest().authenticated())
            .csrf(v -> v.ignoringRequestMatchers(new AntPathRequestMatcher("/api/**")))
            .cors()
            .and()
            .formLogin(Customizer.withDefaults())
            .oauth2ResourceServer(v -> {
                v.jwt()
                 .jwtAuthenticationConverter(this.customizeJwtAuthenticationConverter())
                 .decoder(new JwtDecoderEnhancer(this.jwtDecoder(), authorizationService));
                v.accessDeniedHandler(new Oauth2AccessDeniedHandler())
                 .authenticationEntryPoint(new Oauth2AuthenticationEntryPoint());
            });
        return http.build();
    }

    JwtDecoder jwtDecoder() {
        final var keyPair = this.keyPair();
        final var aPublic = (RSAPublicKey) keyPair.getPublic();
        return NimbusJwtDecoder.withPublicKey(aPublic).build();
    }

    JwtAuthenticationConverter customizeJwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        CustomizeJwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new CustomizeJwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

}
