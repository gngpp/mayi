package com.zf1976.mayi.auth.filter;

//import com.power.common.util.StringUtil;
//import com.zf1976.mayi.auth.SecurityContextHolder;
//import com.zf1976.mayi.common.core.util.JSONUtil;
//import com.zf1976.mayi.common.security.property.SecurityProperties;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.lang.NonNull;
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
//import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Arrays;
//
///**
// * @author ant
// * Create by Ant on 2021/2/18 11:55 PM
// */
//@SuppressWarnings("all")
//public class OAuth2TokenAuthenticationFilter extends OncePerRequestFilter {
//
//    private final Logger log = LoggerFactory.getLogger(this.getClass());
//    private final AntPathMatcher pathMatcher = new AntPathMatcher();
//    private final TokenStore tokenStore = SecurityContextHolder.getShareObject(TokenStore.class);
//    private final TokenExtractor tokenExtractor = new BearerTokenExtractor();
//    private final SecurityProperties properties;
//
//    public OAuth2TokenAuthenticationFilter(SecurityProperties properties) {
//        this.properties = properties;
//    }
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {
//        try {
//            final String accessToken = this.extractAccessToken(request);
//            // token为空、存在放行url，放行
//            if (StringUtils.isEmpty(accessToken) || validateAllowUri(request)) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//            // 校验token 合法性
//            final OAuth2AccessToken oAuth2AccessToken = this.checkTokenLegality(accessToken);
//            final OAuth2Authentication oAuth2Authentication = this.tokenStore.readAuthentication(oAuth2AccessToken);
//            SecurityContext context = SecurityContextHolder.getContext();
//            if (context != null) {
//                if (context.getAuthentication() != null) {
//                    filterChain.doFilter(request, response);
//                    return;
//                }
//                context.setAuthentication(oAuth2Authentication);
//            } else {
//                throw new ServletException("Authentication error at:" + this.getFilterName());
//            }
//        } catch (InvalidTokenException e) {
//            try {
//                this.handleException(e, response);
//            } catch (Exception handleException) {
//                log.error(handleException.getMessage(), handleException.getCause());
//            }
//            return;
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    /**
//     * 提取token
//     *
//     * @param request request
//     * @return
//     */
//    protected String extractAccessToken(HttpServletRequest request) {
//        Authentication authentication = this.tokenExtractor.extract(request);
//        String accessToken;
//        if (authentication instanceof AbstractAuthenticationToken) {
//            accessToken = (String) authentication.getPrincipal();
//            if (!StringUtil.isEmpty(accessToken)) {
//                return accessToken;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 确认 token 合法性
//     * @param accessToken token
//     * @return
//     */
//    protected OAuth2AccessToken checkTokenLegality(String accessToken) {
//        OAuth2AccessToken oAuth2AccessToken = this.tokenStore.readAccessToken(accessToken);
//        if (oAuth2AccessToken == null) {
//            throw new InvalidTokenException("Token was not recognised");
//        } else if (oAuth2AccessToken.isExpired()) {
//            throw new InvalidTokenException("Token has expired");
//        }
//        return oAuth2AccessToken;
//    }
//
//    /**
//     * 处理异常
//     *
//     * @param e exception
//     * @param response response
//     * @throws Exception throw
//     */
//    public void handleException(Exception e, HttpServletResponse response) throws Exception {
//        if (this.log.isDebugEnabled()) {
//            this.logger.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
//        }
//        InvalidTokenException e400 = new InvalidTokenException(e.getMessage());
//        response.setStatus(e400.getHttpErrorCode());
//        JSONUtil.writeValue(response, e400);
//    }
//
//    /**
//     * 验证请求uri是否在放行名单
//     *
//     * @param request request
//     * @return boolean
//     */
//    private boolean validateAllowUri(HttpServletRequest request) {
//        // 匿名放行uri
//        String[] allowUri = properties.getIgnoreUri();
//        // 请求uri
//        String requestUri = request.getRequestURI();
//        return Arrays.stream(allowUri).anyMatch(url -> pathMatcher.match(url, requestUri));
//    }
//}
