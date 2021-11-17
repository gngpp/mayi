package com.zf1976.mayi.auth.filter;

import com.zf1976.mayi.auth.Context;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.util.JSONUtil;
import com.zf1976.mayi.common.security.support.signature.SignatureAuthenticationStrategy;
import com.zf1976.mayi.common.security.support.signature.SignatureState;
import com.zf1976.mayi.common.security.support.signature.StandardSignature;
import com.zf1976.mayi.common.security.support.signature.datasource.ClientDataSourceProvider;
import com.zf1976.mayi.common.security.support.signature.enums.SignaturePattern;
import com.zf1976.mayi.common.security.support.signature.exception.SignatureException;
import com.zf1976.mayi.common.security.support.signature.impl.OpenSignatureAuthenticationStrategy;
import com.zf1976.mayi.common.security.support.signature.impl.SecretSignatureAuthenticationStrategy;
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
