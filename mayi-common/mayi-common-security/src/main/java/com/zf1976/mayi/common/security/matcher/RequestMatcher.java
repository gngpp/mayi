package com.zf1976.mayi.common.security.matcher;

import org.springframework.http.server.reactive.ServerHttpRequest;

import javax.servlet.http.HttpServletRequest;

public interface RequestMatcher extends org.springframework.security.web.util.matcher.RequestMatcher {

    @Override
    boolean matches(HttpServletRequest request);

    @Override
    default MatchResult matcher(HttpServletRequest request) {
        return this.matcher(request);
    }

    boolean matches(ServerHttpRequest request);

    default MatchResult matcher(ServerHttpRequest request) {
        return this.matcher(request);
    }

}
