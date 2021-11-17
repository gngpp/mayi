package com.zf1976.mayi.auth;

import com.zf1976.mayi.auth.service.AuthorizationUserDetails;
import com.zf1976.mayi.common.core.util.RequestUtil;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.common.security.support.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 只适合单机适用
 *
 * @author mac
 */
@Component
public class Context extends SecurityContextHolder {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final Map<Class<?>, Object> CONTENTS_MAP = new ConcurrentReferenceHashMap<>(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);
    private static SecurityProperties securityProperties;

    public static void setShareObject(Class<?> clazz, Object object) {
        Assert.isInstanceOf(clazz, object, "must be an instance of class");
        CONTENTS_MAP.put(clazz, object);
    }

    public static <T> T getShareObject(Class<T> clazz) {
        final var cast = clazz.cast(CONTENTS_MAP.get(clazz));
        CONTENTS_MAP.remove(clazz);
        return cast;
    }

    public static String getIssuer() {
        return securityProperties.getTokenIssuer();
    }

    public static boolean isOwner(String username) {
        return ObjectUtils.nullSafeEquals(Context.securityProperties.getOwner(), username);
    }

    public static void generatorSession(RegisteredClient registeredClient, OAuth2AccessToken oAuth2AccessToken) {
        final var authorizationUserDetails = getShareObject(AuthorizationUserDetails.class);
        final var session = Session.SessionBuilder.newBuilder()
                                                  .id(authorizationUserDetails.getId())
                                                  .username(authorizationUserDetails.getUsername())
                                                  .clientId(registeredClient.getClientId())
                                                  .ip(RequestUtil.getIpAddress())
                                                  .browser(RequestUtil.getUserAgent())
                                                  .operatingSystemType(RequestUtil.getOpsSystemType())
                                                  .ipRegion(RequestUtil.getIpRegion())
                                                  .token(oAuth2AccessToken.getTokenValue())
                                                  .loginTime(oAuth2AccessToken.getIssuedAt())
                                                  .expiredTime(oAuth2AccessToken.getExpiresAt())
                                                  .owner(isOwner(authorizationUserDetails.getUsername()))
                                                  .build();

    }

    @Autowired
    public void setSecurityProperties(SecurityProperties securityProperties) {
        Context.securityProperties = securityProperties;
    }

}
