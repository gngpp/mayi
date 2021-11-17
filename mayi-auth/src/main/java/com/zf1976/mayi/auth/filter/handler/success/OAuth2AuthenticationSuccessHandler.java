package com.zf1976.mayi.auth.filter.handler.success;

import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * @author ant
 * Create by Ant on 2020/9/12 10:03 上午
 */
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public OAuth2AuthenticationSuccessHandler() {

    }

    /**
     * 登陆成功处理
     *
     * @param httpServletRequest  request
     * @param httpServletResponse response
     * @param authentication      认证对象
     * @throws IOException 向上抛异常
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        Assert.isInstanceOf(OAuth2AccessTokenAuthenticationToken.class, authentication);
        OAuth2AccessTokenAuthenticationToken accessTokenAuthentication =
                (OAuth2AccessTokenAuthenticationToken) authentication;

        OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
        OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
        Map<String, Object> additionalParameters = accessTokenAuthentication.getAdditionalParameters();

        OAuth2AccessTokenResponse.Builder builder =
                OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                                         .tokenType(accessToken.getTokenType())
                                         .scopes(accessToken.getScopes());
        if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
            builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
        }
        if (refreshToken != null) {
            builder.refreshToken(refreshToken.getTokenValue());
        }
        if (!CollectionUtils.isEmpty(additionalParameters)) {
            builder.additionalParameters(additionalParameters);
        }
        OAuth2AccessTokenResponse accessTokenResponse = builder.build();
        final var dataResult = DataResult.success(accessTokenResponse);
        JSONUtil.writeValue(httpServletResponse, dataResult);
    }


}
