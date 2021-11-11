package com.zf1976.mayi.auth.config;

import com.zf1976.mayi.auth.enhance.codec.MD5PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * @author mac
 * Create by Ant on 2020/9/1 上午11:32
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final PasswordEncoder passwordEncoder = new MD5PasswordEncoder();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }

}
