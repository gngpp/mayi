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

package com.gngpp.mayi.auth.service.impl;

import com.gngpp.mayi.auth.Context;
import com.gngpp.mayi.auth.enums.AuthenticationState;
import com.gngpp.mayi.auth.exception.IllegalAccessException;
import com.gngpp.mayi.auth.oauth2.authorization.CustomizeOAuthorizationRowMapper;
import com.gngpp.mayi.auth.oauth2.repository.CustomizeRegisteredClientRepository;
import com.gngpp.mayi.auth.service.OAuth2RevokeService;
import org.springframework.jdbc.core.*;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author mac
 * 2021/11/28 星期日 11:14 下午
 */
@Service
@Transactional(readOnly = true, rollbackFor = Throwable.class)
public class OAuth2RevokeServiceImpl implements OAuth2RevokeService {

    private static final String COLUMN_NAMES = "id, "
            + "registered_client_id, "
            + "principal_name, "
            + "authorization_grant_type, "
            + "attributes, "
            + "state, "
            + "authorization_code_value, "
            + "authorization_code_issued_at, "
            + "authorization_code_expires_at,"
            + "authorization_code_metadata,"
            + "access_token_value,"
            + "access_token_issued_at,"
            + "access_token_expires_at,"
            + "access_token_metadata,"
            + "access_token_type,"
            + "access_token_scopes,"
            + "oidc_id_token_value,"
            + "oidc_id_token_issued_at,"
            + "oidc_id_token_expires_at,"
            + "oidc_id_token_metadata,"
            + "refresh_token_value,"
            + "refresh_token_issued_at,"
            + "refresh_token_expires_at,"
            + "refresh_token_metadata";
    private static final String TABLE_NAME = "oauth2_authorization";
    private static final String LOAD_AUTHORIZATION_SQL = "SELECT " + COLUMN_NAMES
            + " FROM " + TABLE_NAME
            + " WHERE ";

    private static final String PRINCIPAL_NAME_AND_CLIENT_ID = "principal_name = ? AND registered_client_id = ?";
    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final RegisteredClientRepository registeredClientRepository;
    private final JdbcOperations jdbcOperations;
    private final RowMapper<OAuth2Authorization> authorizationRowMapper;

    public OAuth2RevokeServiceImpl(OAuth2AuthorizationService oAuth2AuthorizationService,
                                   JdbcOperations jdbcOperations,
                                   RegisteredClientRepository registeredClientRepository) {
        Assert.isInstanceOf(JdbcOAuth2AuthorizationService.class, oAuth2AuthorizationService);
        Assert.isInstanceOf(CustomizeRegisteredClientRepository.class, registeredClientRepository);
        this.jdbcOperations = jdbcOperations;
        this.oAuth2AuthorizationService = oAuth2AuthorizationService;
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationRowMapper = new CustomizeOAuthorizationRowMapper(registeredClientRepository);
    }

    @Override
    @Transactional
    public Void revokeByUsername(String username) {
        Assert.hasText(username, "username cannot be empty");
        final var registerClientID = this.extractClientId();
        if (Objects.isNull(registerClientID)) {
            throw new IllegalAccessException(AuthenticationState.BAD_CREDENTIALS);
        }
        List<SqlParameterValue> parameters = new ArrayList<>();
        parameters.add(new SqlParameterValue(Types.VARCHAR, username));
        parameters.add(new SqlParameterValue(Types.VARCHAR, registerClientID));
        final var authorizationList = this.findBy(PRINCIPAL_NAME_AND_CLIENT_ID, parameters);
        if (!CollectionUtils.isEmpty(authorizationList)) {
            for (OAuth2Authorization oAuth2Authorization : authorizationList) {
                this.oAuth2AuthorizationService.remove(oAuth2Authorization);
            }
        }
        return null;
    }

    @Nullable
    protected String extractClientId() {
        final var clientId = Context.getAuthenticationClientId();
        if (clientId != null) {
            final var registeredClient = this.registeredClientRepository.findByClientId(clientId);
            if (registeredClient != null) {
                return registeredClient.getId();
            }
        }
        return null;
    }

    @SuppressWarnings("SameParameterValue")
    private List<OAuth2Authorization> findBy(String filter, List<SqlParameterValue> parameters) {
        PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(parameters.toArray());
        return this.jdbcOperations.query(LOAD_AUTHORIZATION_SQL + filter, pss, this.authorizationRowMapper);
    }
}
