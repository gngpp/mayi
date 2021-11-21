/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.auth.oauth2.authorization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.mayi.auth.service.AuthorizationUserDetails;
import com.zf1976.mayi.upms.biz.pojo.Department;
import com.zf1976.mayi.upms.biz.pojo.Position;
import com.zf1976.mayi.upms.biz.pojo.Role;
import com.zf1976.mayi.upms.biz.pojo.User;
import com.zf1976.mayi.upms.biz.pojo.enums.GenderEnum;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

/**
 * @author mac
 * 2021/11/13 星期六 1:29 下午
 */
public class CustomizeOAuthorizationRowMapper implements RowMapper<OAuth2Authorization> {
    private final RegisteredClientRepository registeredClientRepository;
    private LobHandler lobHandler = new DefaultLobHandler();
    private ObjectMapper objectMapper = new ObjectMapper();

    public CustomizeOAuthorizationRowMapper(RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.registeredClientRepository = registeredClientRepository;

        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new CoreJackson2Module());
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        this.setUserDetailsMixIn();
    }

    protected void setUserDetailsMixIn() {
        this.objectMapper.addMixIn(Long.class, Object.class);
        this.objectMapper.addMixIn(AuthorizationUserDetails.class, Object.class);
        this.objectMapper.addMixIn(User.class, Object.class);
        this.objectMapper.addMixIn(Role.class, Object.class);
        this.objectMapper.addMixIn(Department.class, Object.class);
        this.objectMapper.addMixIn(Position.class, Object.class);
        this.objectMapper.addMixIn(GenderEnum.class, Object.class);
        this.objectMapper.addMixIn(LinkedHashSet.class, Object.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public OAuth2Authorization mapRow(ResultSet rs, int rowNum) throws SQLException {
        String registeredClientId = rs.getString("registered_client_id");
        RegisteredClient registeredClient = this.registeredClientRepository.findById(registeredClientId);
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + registeredClientId + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);
        String id = rs.getString("id");
        String principalName = rs.getString("principal_name");
        String authorizationGrantType = rs.getString("authorization_grant_type");
        Map<String, Object> attributes = parseMap(rs.getString("attributes"));

        builder.id(id)
               .principalName(principalName)
               .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
               .attributes((attrs) -> attrs.putAll(attributes));

        String state = rs.getString("state");
        if (StringUtils.hasText(state)) {
            builder.attribute(OAuth2ParameterNames.STATE, state);
        }

        String tokenValue;
        Instant tokenIssuedAt;
        Instant tokenExpiresAt;
        byte[] authorizationCodeValue = this.lobHandler.getBlobAsBytes(rs, "authorization_code_value");

        if (authorizationCodeValue != null) {
            tokenValue = new String(authorizationCodeValue, StandardCharsets.UTF_8);
            tokenIssuedAt = rs.getTimestamp("authorization_code_issued_at").toInstant();
            tokenExpiresAt = rs.getTimestamp("authorization_code_expires_at").toInstant();
            Map<String, Object> authorizationCodeMetadata = parseMap(rs.getString("authorization_code_metadata"));

            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    tokenValue, tokenIssuedAt, tokenExpiresAt);
            builder.token(authorizationCode, (metadata) -> metadata.putAll(authorizationCodeMetadata));
        }

        byte[] accessTokenValue = this.lobHandler.getBlobAsBytes(rs, "access_token_value");
        if (accessTokenValue != null) {
            tokenValue = new String(accessTokenValue, StandardCharsets.UTF_8);
            tokenIssuedAt = rs.getTimestamp("access_token_issued_at").toInstant();
            tokenExpiresAt = rs.getTimestamp("access_token_expires_at").toInstant();
            Map<String, Object> accessTokenMetadata = parseMap(rs.getString("access_token_metadata"));
            OAuth2AccessToken.TokenType tokenType = null;
            if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(rs.getString("access_token_type"))) {
                tokenType = OAuth2AccessToken.TokenType.BEARER;
            }

            Set<String> scopes = Collections.emptySet();
            String accessTokenScopes = rs.getString("access_token_scopes");
            if (accessTokenScopes != null) {
                scopes = StringUtils.commaDelimitedListToSet(accessTokenScopes);
            }
            OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, tokenValue, tokenIssuedAt, tokenExpiresAt, scopes);
            builder.token(accessToken, (metadata) -> metadata.putAll(accessTokenMetadata));
        }

        byte[] oidcIdTokenValue = this.lobHandler.getBlobAsBytes(rs, "oidc_id_token_value");
        if (oidcIdTokenValue != null) {
            tokenValue = new String(oidcIdTokenValue, StandardCharsets.UTF_8);
            tokenIssuedAt = rs.getTimestamp("oidc_id_token_issued_at").toInstant();
            tokenExpiresAt = rs.getTimestamp("oidc_id_token_expires_at").toInstant();
            Map<String, Object> oidcTokenMetadata = parseMap(rs.getString("oidc_id_token_metadata"));

            OidcIdToken oidcToken = new OidcIdToken(
                    tokenValue, tokenIssuedAt, tokenExpiresAt, (Map<String, Object>) oidcTokenMetadata.get(OAuth2Authorization.Token.CLAIMS_METADATA_NAME));
            builder.token(oidcToken, (metadata) -> metadata.putAll(oidcTokenMetadata));
        }

        byte[] refreshTokenValue = this.lobHandler.getBlobAsBytes(rs, "refresh_token_value");
        if (refreshTokenValue != null) {
            tokenValue = new String(refreshTokenValue, StandardCharsets.UTF_8);
            tokenIssuedAt = rs.getTimestamp("refresh_token_issued_at").toInstant();
            tokenExpiresAt = null;
            Timestamp refreshTokenExpiresAt = rs.getTimestamp("refresh_token_expires_at");
            if (refreshTokenExpiresAt != null) {
                tokenExpiresAt = refreshTokenExpiresAt.toInstant();
            }
            Map<String, Object> refreshTokenMetadata = parseMap(rs.getString("refresh_token_metadata"));

            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    tokenValue, tokenIssuedAt, tokenExpiresAt);
            builder.token(refreshToken, (metadata) -> metadata.putAll(refreshTokenMetadata));
        }
        return builder.build();
    }

    protected final RegisteredClientRepository getRegisteredClientRepository() {
        return this.registeredClientRepository;
    }

    protected final LobHandler getLobHandler() {
        return this.lobHandler;
    }

    public final void setLobHandler(LobHandler lobHandler) {
        Assert.notNull(lobHandler, "lobHandler cannot be null");
        this.lobHandler = lobHandler;
    }

    protected final ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public final void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "objectMapper cannot be null");
        this.objectMapper = objectMapper;
    }

    private Map<String, Object> parseMap(String data) {
        try {
            return this.objectMapper.readValue(data, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
}
