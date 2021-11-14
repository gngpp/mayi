package com.zf1976.mayi.admin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * @author mac
 * @date 2021/5/25
 */
@Configuration
@EnableWebSecurity
public class EndpointSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final AdminServerProperties adminServer;

    /**
     * Instantiates a new Security secure config.
     *
     * @param adminServer the admin server
     */
    public EndpointSecurityConfigurer(AdminServerProperties adminServer) {
        this.adminServer = adminServer;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        final String adminServerContextPath = this.adminServer.getContextPath();
        successHandler.setDefaultTargetUrl(adminServerContextPath +"/applications");

        http.authorizeRequests()
            .antMatchers(adminServerContextPath + "/assets/**").permitAll()
            .antMatchers(adminServerContextPath + "/success").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().loginPage(adminServerContextPath + "/success").permitAll()
            .successHandler(successHandler).permitAll()
            .and()
            .logout().logoutUrl(adminServerContextPath + "/revoke").permitAll()
            .logoutSuccessUrl(adminServerContextPath + "/revoke").permitAll()
            .and()
            .httpBasic()
            .and()
            // 防止iframe跨域
            .headers().frameOptions().disable()
            .and()
            // 跨域开启
            .cors().disable()
            // 关闭csrf
            .csrf().disable()
            // 关闭记住模式
            .rememberMe().disable();

    }
}
