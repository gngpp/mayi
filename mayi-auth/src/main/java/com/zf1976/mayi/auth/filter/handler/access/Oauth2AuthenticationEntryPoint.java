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
