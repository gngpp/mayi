/*
 *
 *  * Copyright (c) 2021 gngpp
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

package com.gngpp.mayi.auth.filter;

import com.gngpp.mayi.auth.Context;
import com.gngpp.mayi.common.core.foundation.DataResult;
import com.gngpp.mayi.common.core.util.JSONUtil;
import com.gngpp.mayi.common.security.support.signature.SignatureAuthenticationStrategy;
import com.gngpp.mayi.common.security.support.signature.SignatureState;
import com.gngpp.mayi.common.security.support.signature.StandardSignature;
import com.gngpp.mayi.common.security.support.signature.datasource.ClientDataSourceProvider;
import com.gngpp.mayi.common.security.support.signature.enums.SignaturePattern;
import com.gngpp.mayi.common.security.support.signature.exception.SignatureException;
import com.gngpp.mayi.common.security.support.signature.impl.OpenSignatureAuthenticationStrategy;
import com.gngpp.mayi.common.security.support.signature.impl.SecretSignatureAuthenticationStrategy;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 默认支持两种签名认证模式
 *
 * @author mac
 * @date 2021/1/29
 **/
public class SignatureAuthenticationFilter extends OncePerRequestFilter {

    private final Map<SignaturePattern, SignatureAuthenticationStrategy> strategies = new ConcurrentHashMap<>(2);
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private Set<String> ignored;
    public SignatureAuthenticationFilter(ClientDataSourceProvider provider, String ...ignoredUri) {
        this(provider);
        this.ignored = Arrays.stream(ignoredUri).collect(Collectors.toSet());
    }

    public SignatureAuthenticationFilter(ClientDataSourceProvider provider) {
        Assert.notNull(this.strategies,"signature strategy cannot been null");
        this.strategies.put(SignaturePattern.OPEN, new OpenSignatureAuthenticationStrategy(provider));
        this.strategies.put(SignaturePattern.SECRET, new SecretSignatureAuthenticationStrategy(provider));
    }

    /**
     * 签名认证过滤 / 根据请求参数条件选择相应策略
     *
     * @param request request
     * @param response response
     * @param filterChain filter chain
     * @throws ServletException servlet exception
     * @throws IOException io exception
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // 忽略uri
            for (String uri : this.ignored) {
                if (this.antPathMatcher.match(uri, request.getRequestURI())) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
            final SignaturePattern pattern = this.extractSignaturePattern(request);
            SignatureAuthenticationStrategy strategy = this.strategies.get(pattern);
            if (strategy.supports(pattern)) {
                strategy.onAuthenticate(request);
            }
            filterChain.doFilter(request, response);
        } catch (SignatureException e) {
            this.handlerException(response, e);
        }
    }

    private SignaturePattern extractSignaturePattern(HttpServletRequest request) {
        String signaturePattern;
        signaturePattern = request.getHeader(StandardSignature.SIGN_PATTERN);
        if (signaturePattern == null) {
            Context.clearContext();
            throw new SignatureException(SignatureState.NULL_PARAMS_DATA);
        }
        return SignaturePattern.valueOf(signaturePattern);
    }

    private void handlerException(HttpServletResponse response, SignatureException signatureException) {
        Context.clearContext();
        response.setStatus(signatureException.getValue());
        JSONUtil.writeValue(response, DataResult.fail(signatureException.getValue(), signatureException.getReasonPhrase()));
    }

}
