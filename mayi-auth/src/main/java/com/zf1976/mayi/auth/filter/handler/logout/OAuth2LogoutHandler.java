package com.zf1976.mayi.auth.filter.handler.logout;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.zf1976.mayi.auth.LoginUserDetails;
//import com.zf1976.mayi.auth.SecurityContextHolder;
//import com.zf1976.mayi.auth.exception.ExpiredJwtException;
//import com.zf1976.mayi.auth.exception.IllegalAccessException;
//import com.zf1976.mayi.auth.exception.IllegalJwtException;
//import com.zf1976.mayi.common.core.foundation.DataResult;
//import com.zf1976.mayi.common.security.enums.AuthenticationState;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.common.OAuth2RefreshToken;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
//import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.web.authentication.logout.LogoutHandler;
//import org.springframework.util.Assert;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author mac
// * Create by Ant on 2020/10/4 01:03
// */
//@SuppressWarnings("rawtypes")
//public class OAuth2LogoutHandler implements LogoutHandler {
//
//    public final Logger log = LoggerFactory.getLogger("[Oauth2LogoutHandler]");
//    private final TokenStore tokenStore;
//    private final TokenExtractor tokenExtractor;
//
//    public OAuth2LogoutHandler() {
//        TokenStore shareObject = SecurityContextHolder.getShareObject(TokenStore.class);
//        Assert.notNull(shareObject, "share object RedisStore is cannot been null!");
//        this.tokenStore = shareObject;
//        this.tokenExtractor = new BearerTokenExtractor();
//    }
//
//    @Override
//    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        try {
//            this.support(request);
//
//            String accessToken = this.extractAccessToken(request);
//            final OAuth2AccessToken oAuth2AccessToken = this.tokenStore.readAccessToken(accessToken);
//            final OAuth2RefreshToken oAuth2RefreshToken = oAuth2AccessToken.getRefreshToken();
//            // 删除access token
//            this.tokenStore.removeAccessToken(oAuth2AccessToken);
//            // 删除refresh token
//            this.tokenStore.removeRefreshToken(oAuth2RefreshToken);
//        } catch (AuthenticationException e) {
//            try {
//                this.unsuccessfulLogoutHandler(request, response, e);
//            } catch (IOException ioException) {
//                log.error(ioException.getMessage(), ioException.getCause());
//            }
//            throw e;
//        }
//    }
//
//    /**
//     * 校验请求方法
//     *
//     * @param request request
//     */
//    private void support(HttpServletRequest request) {
//        final String method = request.getMethod();
//        if (!method.equals(HttpMethod.POST.name())) {
//            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
//        }
//    }
//
//    /**
//     * 提取Session ID
//     *
//     * @param authentication 认证
//     * @return {@link long}
//     */
//    @Deprecated
//    private long extractSessionId(Authentication authentication) {
//        Assert.isInstanceOf(OAuth2Authentication.class, authentication);
//        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
//        LoginUserDetails details = (LoginUserDetails) oAuth2Authentication.getUserAuthentication()
//                                                                          .getPrincipal();
//        if (!oAuth2Authentication.isAuthenticated() || details == null) {
//            throw new ExpiredJwtException(AuthenticationState.ILLEGAL_ACCESS);
//        }
//        Assert.notNull(details.getId(), "id cannot been null");
//        return details.getId();
//    }
//
//    /**
//     * 提取token
//     *
//     * @param request 请求
//     * @return {@link String}
//     */
//    private String extractAccessToken(HttpServletRequest request) {
//        final Object principal = this.tokenExtractor.extract(request)
//                                                    .getPrincipal();
//        if (!(principal instanceof String)) {
//            throw new ExpiredJwtException(AuthenticationState.EXPIRED_JWT);
//        }
//        return ((String) principal);
//    }
//
//    /**
//     * 登出失败处理
//     *
//     * @param httpServletRequest  request
//     * @param httpServletResponse response
//     * @param e                   异常
//     * @throws IOException 向上抛异常
//     */
//    private void unsuccessfulLogoutHandler(HttpServletRequest httpServletRequest,
//                                           HttpServletResponse httpServletResponse,
//                                           AuthenticationException e) throws IOException {
//        DataResult fail;
//        if (e instanceof ExpiredJwtException) {
//            ExpiredJwtException exception = (ExpiredJwtException) e;
//            fail = DataResult.fail(exception.getValue(), exception.getReasonPhrase());
//        } else if (e instanceof IllegalAccessException) {
//            IllegalAccessException exception = (IllegalAccessException) e;
//            fail = DataResult.fail(exception.getValue(), exception.getReasonPhrase());
//        } else if (e instanceof IllegalJwtException) {
//            IllegalJwtException exception = (IllegalJwtException) e;
//            fail = DataResult.fail(exception.getValue(), exception.getReasonPhrase());
//        } else if (e instanceof AuthenticationServiceException) {
//            AuthenticationServiceException exception = (AuthenticationServiceException) e;
//            fail = DataResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
//        } else {
//            fail = DataResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//        }
//        httpServletResponse.setStatus(HttpStatus.OK.value());
//        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
//        fail.setPath(httpServletRequest.getRequestURI());
//        final ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValue(httpServletResponse.getOutputStream(), fail);
//    }
//
//}
