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

package com.zf1976.mayi.auth.filter.handler.access;

import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.util.JSONUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mac
 * Create by Ant on 2020/9/3 下午8:52
 */
@SuppressWarnings("rawtypes")
public class Oauth2AuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 当用户访问安全的资源时 未提供凭证 或者 无效凭证时 发送401处理
     *
     * @param httpServletRequest  request
     * @param httpServletResponse response
     * @param e                   exception
     */
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) {
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        DataResult fail = DataResult.fail(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        fail.setPath(httpServletRequest.getRequestURI());
        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        JSONUtil.writeValue(httpServletResponse, fail);
    }
}
