package com.zf1976.ant.auth.config;

import com.zf1976.ant.auth.handler.logout.Oauth2LogoutHandler;
import com.zf1976.ant.auth.handler.logout.Oauth2LogoutSuccessHandler;
import com.zf1976.ant.common.core.dev.SecurityProperties;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;

/**
 * @author mac
 * Create by Ant on 2020/9/1 上午11:32
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final SecurityProperties securityProperties;

    public WebSecurityConfigurer(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 防止覆盖
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭CSRF
        http.csrf().disable()
            // 允许跨域
            .cors()
            .and()
            // 登出处理
            .logout()
            .logoutUrl("/oauth/logout")
            .addLogoutHandler(new Oauth2LogoutHandler())
            .logoutSuccessHandler(new Oauth2LogoutSuccessHandler())
            .and()
            // 防止iframe跨域
            .headers()
            .frameOptions()
            .disable()
            .and()
            // 关闭会话创建
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
            // 放行OPTIONS请求
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // 白名单
            .antMatchers(securityProperties.getAllowUri()).permitAll()
            .antMatchers("/oauth/**").permitAll()
            .anyRequest()
            .authenticated();
             //动态数据源过滤
           // .addFilterBefore(new DynamicSecurityFilter(), FilterSecurityInterceptor.class)
             // 用户名密码验证
//            .addFilterAt(new LoginAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
             // 签名过滤
           // .addFilterBefore(new SignatureAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
             // Jwt token过滤
            //.addFilterAfter(new SecurityJwtAuthenticationFilter(), SecurityAuthenticationProcessingFilter.class);
    }
}