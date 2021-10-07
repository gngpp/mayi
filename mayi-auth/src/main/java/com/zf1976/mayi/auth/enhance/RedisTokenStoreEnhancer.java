package com.zf1976.mayi.auth.enhance;

import com.zf1976.mayi.auth.LoginUserDetails;
import com.zf1976.mayi.auth.SecurityContextHolder;
import com.zf1976.mayi.auth.enhance.serialize.JacksonSerializationStrategy;
import com.zf1976.mayi.common.core.constants.AuthGranterTypeConstants;
import com.zf1976.mayi.common.core.util.RequestUtil;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.common.security.support.session.Session;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 自带Session状态
 *
 * @author mac
 * @date 2021/2/16
 **/
public class RedisTokenStoreEnhancer implements TokenStore {

    private static final boolean springDataRedis_2_0 = ClassUtils.isPresent("org.springframework.data.redis.connection.RedisStandaloneConfiguration", RedisTokenStore.class.getClassLoader());
    private final RedisConnectionFactory connectionFactory;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    private final SecurityProperties properties;
    private RedisTokenStoreSerializationStrategy jdkSerializationStrategy = new JdkSerializationStrategy();
    private RedisTokenStoreSerializationStrategy jacksonSerializationStrategy = new JacksonSerializationStrategy();
    private Method redisConnectionSet_2_0;
    private final TokenStore tokenStore;

    public RedisTokenStoreEnhancer(RedisConnectionFactory connectionFactory, SecurityProperties properties) {
        this.properties = properties;
        this.connectionFactory = connectionFactory;
        if (springDataRedis_2_0) {
            this.loadRedisConnectionMethods_2_0();
        }
        this.tokenStore = new RedisTokenStore(connectionFactory);
    }

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    public void setJdkSerializationStrategy(RedisTokenStoreSerializationStrategy jdkSerializationStrategy) {
        this.jdkSerializationStrategy = jdkSerializationStrategy;
    }

    public void setJacksonSerializationStrategy(RedisTokenStoreSerializationStrategy jacksonSerializationStrategy) {
        this.jacksonSerializationStrategy = jacksonSerializationStrategy;
    }


    private void loadRedisConnectionMethods_2_0() {
        this.redisConnectionSet_2_0 = ReflectionUtils.findMethod(RedisConnection.class, "set", byte[].class, byte[].class);
    }

    private RedisConnection getConnection() {
        return this.connectionFactory.getConnection();
    }

    /**
     * 序列化对象
     *
     * @param object object
     * @return {@link byte[]}
     * @date 2021-05-16 22:57:03
     */
    private byte[] jdkSerialize(Object object) {
        return this.jdkSerializationStrategy.serialize(object);
    }

    /**
     * 序列化对象
     *
     * @param object object
     * @return {@link byte[]}
     */
    private byte[] jacksonSerialize(Object object) {
        return this.jacksonSerializationStrategy.serialize(object);
    }

    /**
     * 序列化key
     *
     * @param object key
     * @return {@link byte[]}
     */
    private byte[] jdkSerializeKey(String object) {
        return this.jdkSerialize(object);
    }

    /**
     * 序列化key
     *
     * @param object key
     * @return {@link byte[]}
     */
    private byte[] jacksonSerializeKey(String object) {
        return this.jacksonSerializationStrategy.serialize(object);
    }

    /**
     * 反序列化Session
     *
     * @param bytes 字节数组key
     * @return {@link Session}
     */
    private Session jacksonDeserializeSession(byte[] bytes) {
        return this.jacksonSerializationStrategy.deserialize(bytes, Session.class);
    }

    /**
     * 反序列化Access Token
     *
     * @param bytes 字节数组key
     * @return {@link OAuth2AccessToken}
     */
    private OAuth2AccessToken jdkDeserializeAccessToken(byte[] bytes) {
        return this.jdkSerializationStrategy.deserialize(bytes, OAuth2AccessToken.class);
    }

    /**
     * 反序列化OAuth2 认证信息
     *
     * @param bytes 字节数组key
     */
    private OAuth2Authentication jdkDeserializeAuthentication(byte[] bytes) {
        return this.jdkSerializationStrategy.deserialize(bytes, OAuth2Authentication.class);
    }

    /**
     * 反序列化Refresh Token
     *
     * @param bytes 字节数组
     * @return {@link OAuth2RefreshToken}
     */
    private OAuth2RefreshToken jdkDeserializeRefreshToken(byte[] bytes) {
        return this.jdkSerializationStrategy.deserialize(bytes, OAuth2RefreshToken.class);
    }

    /**
     * 序列户String
     *
     * @param string string
     * @return {@link byte[]}
     */
    private byte[] jdkSerialize(String string) {
        return this.jdkSerializationStrategy.serialize(string);
    }

    /**
     * 反序列化String
     *
     * @param bytes 字节数组key
     * @return {@link String}
     */
    private String jdkDeserializeString(byte[] bytes) {
        return this.jdkSerializationStrategy.deserializeString(bytes);
    }

    /**
     * 根据OAuth2认证信息获取AccessToken
     *
     * @param authentication 认证信息
     * @return {@link OAuth2AccessToken}
     */
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = this.authenticationKeyGenerator.extractKey(authentication);
        byte[] serializedKey = this.jdkSerializeKey("auth_to_access:" + key);
        byte[] bytes;

        try (RedisConnection conn = this.getConnection()) {
            bytes = conn.get(serializedKey);
        }

        OAuth2AccessToken accessToken = this.jdkDeserializeAccessToken(bytes);
        if (accessToken != null) {
            OAuth2Authentication storedAuthentication = this.readAuthentication(accessToken.getValue());
            if (storedAuthentication == null || !key.equals(this.authenticationKeyGenerator.extractKey(storedAuthentication))) {
                this.storeAccessToken(accessToken, authentication);
            }
        }

        return accessToken;
    }

    /**
     * 读取Authentication
     *
     * @param token Access token
     * @return {@link OAuth2Authentication}
     */
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return this.readAuthentication(token.getValue());
    }

    /**
     * 读取Authentication
     *
     * @param token token
     * @return {@link OAuth2Authentication}
     */
    public OAuth2Authentication readAuthentication(String token) {

        byte[] bytes;
        try (RedisConnection conn = this.getConnection()) {
            bytes = conn.get(this.jdkSerializeKey("auth:" + token));
        }

        return this.jdkDeserializeAuthentication(bytes);
    }

    /**
     * 根据Refresh Token 读取Authentication
     *
     * @param token OAuth2RefreshToken
     * @return {@link OAuth2Authentication}
     */
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return this.readAuthenticationForRefreshToken(token.getValue());
    }

    /**
     * 根据Refresh Token 读取Authentication
     *
     * @param token refresh token
     * @return {@link OAuth2Authentication}
     */
    public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
        RedisConnection conn = this.getConnection();

        OAuth2Authentication var5;
        try {
            byte[] bytes = conn.get(this.jdkSerializeKey("refresh_auth:" + token));
            var5 = this.jdkDeserializeAuthentication(bytes);
        } finally {
            conn.close();
        }

        return var5;
    }

    /**
     * 存储AccessToken
     *
     * @param token          {@link OAuth2AccessToken}
     * @param authentication {@link OAuth2Authentication}
     */
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        if (authentication.getOAuth2Request().getGrantType().equals(AuthGranterTypeConstants.CLIENT_CREDENTIALS)) {
            this.tokenStore.storeAccessToken(token, authentication);
            return;
        }
        // 认证成功信息细节
        LoginUserDetails details = (LoginUserDetails) authentication.getPrincipal();
        // 保存认证成功细节
        authentication.setDetails(details);
        // 保存认证
        SecurityContextHolder.getContext()
                             .setAuthentication(authentication);
        // 序列化Session
        byte[] serializeSession = this.jacksonSerialize(this.generationSession(token, authentication));
        // 序列化id-token键
        byte[] idToSessionKey = this.jacksonSerializeKey(properties.getPrefixSessionId() + details.getId());
        // 序列化AccessToken
        byte[] serializedAccessToken = this.jdkSerialize(token);
        // 序列化Authentication
        byte[] serializedAuth = this.jdkSerialize(authentication);
        // 序列化session-token键
        byte[] accessToSessionKey = this.jacksonSerializeKey(properties.getPrefixSessionToken() + token.getValue());
        // 序列化access-token键
        byte[] accessKey = this.jdkSerializeKey("access:" + token.getValue());
        // 序列化auth-token键
        byte[] authKey = this.jdkSerializeKey("auth:" + token.getValue());
        // 序列化auth-to-access键
        byte[] authToAccessKey = this.jdkSerializeKey("auth_to_access:" + this.authenticationKeyGenerator.extractKey(authentication));
        // 序列化批准键
        byte[] approvalKey = this.jdkSerializeKey("uname_to_access:" + getApprovalKey(authentication));
        // 序列化client-id-to-access键
        byte[] clientId = this.jdkSerializeKey("client_id_to_access:" + authentication.getOAuth2Request()
                                                                                      .getClientId());
        try (RedisConnection conn = this.getConnection()) {
            conn.openPipeline();
            // Spring boot 2.0版本
            if (springDataRedis_2_0) {
                try {
                    this.redisConnectionSet_2_0.invoke(conn, idToSessionKey, serializeSession);
                    this.redisConnectionSet_2_0.invoke(conn, accessToSessionKey, serializeSession);
                    this.redisConnectionSet_2_0.invoke(conn, accessKey, serializedAccessToken);
                    this.redisConnectionSet_2_0.invoke(conn, authKey, serializedAuth);
                    this.redisConnectionSet_2_0.invoke(conn, authToAccessKey, serializedAccessToken);
                } catch (Exception var24) {
                    throw new RuntimeException(var24);
                }
            } else {
                // 只要是拥有token/authentication都可序列化对方

                // 以id_to_session:前缀id指向Session
                conn.set(idToSessionKey, serializeSession);
                // 以token_to_session:前缀token指向Session
                conn.set(accessToSessionKey, serializeSession);
                // 以access:前缀token指向OAuth2AccessToken
                conn.set(accessKey, serializedAccessToken);
                // 以auth:前缀token指向OAuth2Authentication
                conn.set(authKey, serializedAuth);
                // 以auth_to_access:前缀authentication指向OAuth2AccessToken
                conn.set(authToAccessKey, serializedAccessToken);
            }

            if (!authentication.isClientOnly()) {
                conn.sAdd(approvalKey, new byte[][]{serializedAccessToken});
            }

            conn.sAdd(clientId, new byte[][]{serializedAccessToken});
            if (token.getExpiration() != null) {
                int seconds = token.getExpiresIn();
                conn.expire(idToSessionKey, seconds);
                conn.expire(accessToSessionKey, seconds);
                conn.expire(accessKey, seconds);
                conn.expire(authKey, seconds);
                conn.expire(authToAccessKey, seconds);
                conn.expire(clientId, seconds);
                conn.expire(approvalKey, seconds);
            }

            OAuth2RefreshToken refreshToken = token.getRefreshToken();
            if (refreshToken != null && refreshToken.getValue() != null) {
                byte[] refresh = this.jdkSerialize(token.getRefreshToken()
                                                        .getValue());
                byte[] auth = this.jdkSerialize(token.getValue());
                byte[] refreshToAccessKey = this.jdkSerializeKey("refresh_to_access:" + token.getRefreshToken()
                                                                                             .getValue());
                byte[] accessToRefreshKey = this.jdkSerializeKey("access_to_refresh:" + token.getValue());
                if (springDataRedis_2_0) {
                    try {
                        this.redisConnectionSet_2_0.invoke(conn, refreshToAccessKey, auth);
                        this.redisConnectionSet_2_0.invoke(conn, accessToRefreshKey, refresh);
                    } catch (Exception var23) {
                        throw new RuntimeException(var23);
                    }
                } else {
                    conn.set(refreshToAccessKey, auth);
                    conn.set(accessToRefreshKey, refresh);
                }

                refreshTokenInstanceofCheck(conn, refreshToken, refreshToAccessKey, accessToRefreshKey);
            }

            conn.closePipeline();
        }

    }

    private void refreshTokenInstanceofCheck(RedisConnection conn,
                                             OAuth2RefreshToken refreshToken,
                                             byte[] refreshToAccessKey, byte[] accessToRefreshKey) {
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
            Date expiration = expiringRefreshToken.getExpiration();
            if (expiration != null) {
                int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                                  .intValue();
                conn.expire(refreshToAccessKey, seconds);
                conn.expire(accessToRefreshKey, seconds);
            }
        }
    }

    private static String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? "" : authentication.getUserAuthentication().getName();
        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private static String getApprovalKey(String clientId, String userName) {
        return clientId + (userName == null ? "" : ":" + userName);
    }

    public void removeAccessToken(OAuth2AccessToken accessToken) {
        this.removeAccessToken(accessToken.getValue());
    }

    public OAuth2AccessToken readAccessToken(String tokenValue) {
        byte[] key = this.jdkSerializeKey("access:" + tokenValue);
        byte[] bytes;

        try (RedisConnection conn = this.getConnection()) {
            bytes = conn.get(key);
        }

        return this.jdkDeserializeAccessToken(bytes);
    }

    public void removeAccessToken(String tokenValue) {
        byte[] accessKey = this.jdkSerializeKey("access:" + tokenValue);
        byte[] authKey = this.jdkSerializeKey("auth:" + tokenValue);
        byte[] accessToRefreshKey = this.jdkSerializeKey("access_to_refresh:" + tokenValue);
        byte[] accessToSessionKey = this.jacksonSerializeKey(this.properties.getPrefixSessionToken() + tokenValue);
        // 删除Session
        try (RedisConnection conn = this.getConnection()) {
            byte[] sessionBytes = conn.get(accessToSessionKey);
            Session session = this.jacksonDeserializeSession(sessionBytes);
            byte[] idToSessionKey = this.jacksonSerializeKey(this.properties.getPrefixSessionId() + session.getId());
            conn.openPipeline();
            conn.del(idToSessionKey);
            conn.del(accessToSessionKey);
            conn.closePipeline();
        }

        try (RedisConnection conn = this.getConnection()) {
            conn.openPipeline();
            conn.get(accessKey);
            conn.get(authKey);
            conn.del(new byte[][]{accessKey});
            conn.del(new byte[][]{accessToRefreshKey});
            conn.del(new byte[][]{authKey});
            List<Object> results = conn.closePipeline();
            // 获取OAuthAccessToken
            byte[] access = (byte[]) results.get(0);
            // 获取OAuthAuthentication
            byte[] auth = (byte[]) results.get(1);
            OAuth2Authentication authentication = this.jdkDeserializeAuthentication(auth);
            if (authentication != null) {
                String key = this.authenticationKeyGenerator.extractKey(authentication);
                byte[] authToAccessKey = this.jdkSerializeKey("auth_to_access:" + key);
                byte[] unameKey = this.jdkSerializeKey("uname_to_access:" + getApprovalKey(authentication));
                byte[] clientId = this.jdkSerializeKey("client_id_to_access:" + authentication.getOAuth2Request()
                                                                                              .getClientId());
                conn.openPipeline();
                conn.del(new byte[][]{authToAccessKey});
                conn.sRem(unameKey, new byte[][]{access});
                conn.sRem(clientId, new byte[][]{access});
                conn.del(new byte[][]{this.jdkSerialize("access:" + key)});
                conn.closePipeline();
            }
        }

    }

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        byte[] refreshKey = this.jdkSerializeKey("refresh:" + refreshToken.getValue());
        byte[] refreshAuthKey = this.jdkSerializeKey("refresh_auth:" + refreshToken.getValue());
        byte[] serializedRefreshToken = this.jdkSerialize(refreshToken);

        try (RedisConnection conn = this.getConnection()) {
            conn.openPipeline();
            if (springDataRedis_2_0) {
                try {
                    this.redisConnectionSet_2_0.invoke(conn, refreshKey, serializedRefreshToken);
                    this.redisConnectionSet_2_0.invoke(conn, refreshAuthKey, this.jdkSerialize(authentication));
                } catch (Exception var13) {
                    throw new RuntimeException(var13);
                }
            } else {
                conn.set(refreshKey, serializedRefreshToken);
                conn.set(refreshAuthKey, this.jdkSerialize(authentication));
            }

            refreshTokenInstanceofCheck(conn, refreshToken, refreshKey, refreshAuthKey);

            conn.closePipeline();
        }

    }

    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        byte[] key = this.jdkSerializeKey("refresh:" + tokenValue);
        byte[] bytes;


        try (RedisConnection conn = this.getConnection()) {
            bytes = conn.get(key);
        }

        return this.jdkDeserializeRefreshToken(bytes);
    }

    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        this.removeRefreshToken(refreshToken.getValue());
    }

    public void removeRefreshToken(String tokenValue) {
        byte[] refreshKey = this.jdkSerializeKey("refresh:" + tokenValue);
        byte[] refreshAuthKey = this.jdkSerializeKey("refresh_auth:" + tokenValue);
        byte[] refresh2AccessKey = this.jdkSerializeKey("refresh_to_access:" + tokenValue);
        byte[] access2RefreshKey = this.jdkSerializeKey("access_to_refresh:" + tokenValue);

        try (RedisConnection conn = this.getConnection()) {
            conn.openPipeline();
            conn.del(new byte[][]{refreshKey});
            conn.del(new byte[][]{refreshAuthKey});
            conn.del(new byte[][]{refresh2AccessKey});
            conn.del(new byte[][]{access2RefreshKey});
            conn.closePipeline();
        }

    }

    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        this.removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    private void removeAccessTokenUsingRefreshToken(String refreshToken) {
        byte[] key = this.jdkSerializeKey("refresh_to_access:" + refreshToken);
        List<Object> results;

        try (RedisConnection conn = this.getConnection()) {
            conn.openPipeline();
            conn.get(key);
            conn.del(new byte[][]{key});
            results = conn.closePipeline();
        }

        byte[] bytes = (byte[]) results.get(0);
        String accessToken = this.jdkDeserializeString(bytes);
        if (accessToken != null) {
            this.removeAccessToken(accessToken);
        }

    }

    private List<byte[]> getByteLists(byte[] approvalKey, RedisConnection conn) {
        Long size = conn.sCard(approvalKey);
        assert size != null;
        List<byte[]> byteList = new ArrayList<>(size.intValue());
        Cursor<byte[]> cursor = conn.sScan(approvalKey, ScanOptions.NONE);

        while(cursor.hasNext()) {
            byteList.add(cursor.next());
        }

        return byteList;
    }

    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        byte[] approvalKey = this.jdkSerializeKey("uname_to_access:" + getApprovalKey(clientId, userName));
        return getOAuth2AccessTokenList(approvalKey);
    }

    private Collection<OAuth2AccessToken> getOAuth2AccessTokenList(byte[] approvalKey) {
        List<byte[]> byteList;

        try (RedisConnection conn = this.getConnection()) {
            byteList = this.getByteLists(approvalKey, conn);
        }

        if (byteList.size() != 0) {
            List<OAuth2AccessToken> accessTokens = new ArrayList<>(byteList.size());

            for (byte[] bytes : byteList) {
                OAuth2AccessToken accessToken = this.jdkDeserializeAccessToken(bytes);
                accessTokens.add(accessToken);
            }

            return Collections.unmodifiableCollection(accessTokens);
        } else {
            return Collections.emptySet();
        }
    }

    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        byte[] key = this.jdkSerializeKey("client_id_to_access:" + clientId);
        return getOAuth2AccessTokenList(key);
    }

    private Session generationSession(OAuth2AccessToken token, OAuth2Authentication authentication) {
        Object principal = authentication.getUserAuthentication()
                                         .getPrincipal();
        LoginUserDetails details = (LoginUserDetails) principal;
        String clientId = authentication.getOAuth2Request()
                                        .getClientId();
        return new Session()
                .setId(details.getId())
                .setUsername(details.getUsername())
                .setClientId(clientId)
                .setIp(RequestUtil.getIpAddress())
                .setIpRegion(RequestUtil.getIpRegion())
                .setToken(token.getValue())
                .setBrowser(RequestUtil.getUserAgent())
                .setExpiredTime(token.getExpiration())
                .setPermissions(AuthorityUtils.authorityListToSet(authentication.getAuthorities()))
                .setLoginTime(new Date())
                .setOperatingSystemType(RequestUtil.getOpsSystemType())
                .setOwner(this.properties.getOwner()
                                         .equals(details.getUsername()));

    }

}
