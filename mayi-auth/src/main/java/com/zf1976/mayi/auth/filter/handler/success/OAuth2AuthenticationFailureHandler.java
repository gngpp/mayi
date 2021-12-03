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

package com.zf1976.mayi.auth.filter.handler.success;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.mayi.auth.exception.*;
import com.zf1976.mayi.common.core.foundation.DataResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ant
 * Create by Ant on 2020/9/12 10:04 上午
 */
@SuppressWarnings("rawtypes")
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * 认证失败处理
     *
     * @param httpServletRequest  request
     * @param httpServletResponse response
     * @param e                   exception
     * @throws IOException 向上抛异常
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        SecurityContextHolder.clearContext();
        ObjectMapper objectMapper = new ObjectMapper();
        DataResult result = null;
        if (e instanceof RsaDecryptException) {
            RsaDecryptException exception = (RsaDecryptException) e;
            httpServletResponse.setStatus(exception.getValue());
            result = DataResult.fail(exception.getValue(), exception.getReasonPhrase());
        }
        if (e instanceof CaptchaException) {
            CaptchaException exception = (CaptchaException) e;
            httpServletResponse.setStatus(exception.getValue());
            result = DataResult.fail(exception.getValue(), exception.getReasonPhrase());
        }
        if (e instanceof BadCredentialsException) {
            BadCredentialsException exception = (BadCredentialsException) e;
            httpServletResponse.setStatus(exception.getValue());
            result = DataResult.fail(exception.getValue(), exception.getReasonPhrase());
        }
        if (e instanceof PasswordException) {
            PasswordException exception = (PasswordException) e;
            httpServletResponse.setStatus(exception.getValue());
            result = DataResult.fail(exception.getValue(), exception.getReasonPhrase());
        }
        if (e instanceof UserNotFountException) {
            UserNotFountException exception = (UserNotFountException) e;
            httpServletResponse.setStatus(exception.getValue());
            result = DataResult.fail(exception.getValue(), exception.getReasonPhrase());
        }
        if (e instanceof AuthenticationServiceException) {
            AuthenticationServiceException exception = (AuthenticationServiceException) e;
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            result = DataResult.fail(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        }
        if (e != null && result == null) {
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result = DataResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
        Assert.notNull(result, "result cannot been null");
        result.setPath(httpServletRequest.getRequestURI());
        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        objectMapper.writeValue(httpServletResponse.getOutputStream(), result);
    }
}
