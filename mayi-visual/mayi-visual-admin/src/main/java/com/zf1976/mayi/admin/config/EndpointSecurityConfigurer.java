/*
 *
 *  * Copyright (c) 2021 zf1976
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

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
