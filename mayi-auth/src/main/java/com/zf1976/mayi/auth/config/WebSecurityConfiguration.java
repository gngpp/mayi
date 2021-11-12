package com.zf1976.mayi.auth.config;

import com.zf1976.mayi.auth.enhance.codec.MD5PasswordEncoder;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * @author mac
 * Create by Ant on 2020/9/1 上午11:32
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final SecurityProperties properties;

    private final PasswordEncoder passwordEncoder = new MD5PasswordEncoder();

    public WebSecurityConfiguration(SecurityProperties securityProperties) {
        this.properties = securityProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }

    /**
     * 标准安全处理
     *
     * @param http http security
     * @return {@link SecurityFilterChain}
     * @throws Exception e
     */
    @Bean
    @Order(2)
    public SecurityFilterChain standardSecurityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off

        // 授权认证处理
        http.authorizeHttpRequests((authorize) -> authorize
                            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            // 兼容旧版本接口
                            .antMatchers("/oauth2/token_key").permitAll()
                            .antMatchers(properties.getIgnoreUri()).permitAll()
                            .anyRequest().authenticated()
                                  )
            .formLogin(Customizer.withDefaults());
        return http.build();
    }


}
