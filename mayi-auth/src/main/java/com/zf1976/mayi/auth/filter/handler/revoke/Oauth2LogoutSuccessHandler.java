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

package com.zf1976.mayi.auth.filter.handler.revoke;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.mayi.auth.core.AuthorizationUserDetails;
import com.zf1976.mayi.common.core.foundation.DataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author mac
 * Create by Ant on 2020/10/4 00:18
 */
public class Oauth2LogoutSuccessHandler implements LogoutSuccessHandler {

    public final Logger log = LoggerFactory.getLogger("log-[Logout]-" + this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpServletResponse.setStatus(HttpStatus.OK.value());
        objectMapper.writeValue(httpServletResponse.getOutputStream(), DataResult.success("Have to log out！"));
        log.info("Authentication principle {} is revoke", this.extractUsername(authentication));
    }

    private String extractUsername(Authentication authentication) {
        AuthorizationUserDetails userDetails = (AuthorizationUserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
