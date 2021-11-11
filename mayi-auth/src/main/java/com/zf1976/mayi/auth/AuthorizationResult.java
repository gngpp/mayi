package com.zf1976.mayi.auth;

import com.zf1976.mayi.upms.biz.pojo.User;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

/**
 * @author mac
 * @date 2021/4/9
 */
public class AuthorizationResult {

    private final OAuth2AccessToken oAuth2AccessToken;
    private final User user;

    public AuthorizationResult(OAuth2AccessToken oAuth2AccessToken, User user) {
        this.oAuth2AccessToken = oAuth2AccessToken;
        this.user = user;
    }

    public OAuth2AccessToken getOAuth2AccessToken() {
        return oAuth2AccessToken;
    }

    public User getUser() {
        return user;
    }
}
