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

package com.zf1976.mayi.upms.biz.security.config;

import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.upms.biz.security.filter.DynamicSecurityFilter;
import com.zf1976.mayi.upms.biz.security.oauth2.CustomizeNimbusOpaqueTokenIntrospector;
import com.zf1976.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;


/**
 * @author mac
 * @date 2021/6/9
 */
@Configuration
@EnableWebSecurity
public class ResourceServerConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}")
    private String introspectionUri;

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
    private String clientSecret;

    private final DynamicDataSourceService dynamicDataSourceService;
    private final SecurityProperties securityProperties;

    public ResourceServerConfiguration(DynamicDataSourceService dynamicDataSourceService, SecurityProperties securityProperties) {
        this.dynamicDataSourceService = dynamicDataSourceService;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests((authorize) -> authorize
                            .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .mvcMatchers(
                                    "/api/authorities/**",
                                    "/api/test/**",
                                    "/actuator/**").permitAll()
                            .mvcMatchers(this.securityProperties.getIgnoreUri()).permitAll()
                            .anyRequest().authenticated()
                              )
            .oauth2ResourceServer(oauth2 -> oauth2
                            .opaqueToken((opaque) -> opaque
                                            .introspector(new CustomizeNimbusOpaqueTokenIntrospector(this.introspectionUri, this.clientId, this.clientSecret))
                                        )
                                 );


        // turn off Http Basic authentication
        http.httpBasic().disable()
            .headers().frameOptions().disable()
            .and()
            // csrf intercepts all post requests by default, ignoring api
            .csrf(v -> v.ignoringAntMatchers("/api/**", "/actuator/**"))
            .cors();

        // dynamic permission filtering
        http.addFilterAt(new DynamicSecurityFilter(this.securityProperties, this.dynamicDataSourceService), FilterSecurityInterceptor.class);
    }


}
