package com.zf1976.mayi.auth.service.impl;

import com.zf1976.mayi.auth.oauth2.authorization.CustomizeOAuthorizationRowMapper;
import com.zf1976.mayi.auth.oauth2.repository.CustomizeRegisteredClientRepository;
import com.zf1976.mayi.auth.service.OAuth2RevokeService;
import org.springframework.jdbc.core.*;
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

    private static final String PRINCIPAL_NAME = "principal_name = ?";

    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final JdbcOperations jdbcOperations;
    private final RowMapper<OAuth2Authorization> authorizationRowMapper;

    public OAuth2RevokeServiceImpl(OAuth2AuthorizationService oAuth2AuthorizationService,
                                   JdbcOperations jdbcOperations,
                                   RegisteredClientRepository registeredClientRepository) {
        Assert.isInstanceOf(JdbcOAuth2AuthorizationService.class, oAuth2AuthorizationService);
        Assert.isInstanceOf(CustomizeRegisteredClientRepository.class, registeredClientRepository);
        this.jdbcOperations = jdbcOperations;
        this.oAuth2AuthorizationService = oAuth2AuthorizationService;
        this.authorizationRowMapper = new CustomizeOAuthorizationRowMapper(registeredClientRepository);
    }

    @Override
    @Transactional
    public Void revokeByUsername(String username) {
        Assert.hasText(username, "username cannot be empty");
        List<SqlParameterValue> parameters = new ArrayList<>();
        parameters.add(new SqlParameterValue(Types.VARCHAR, username));
        final var authorizationList = this.findBy(PRINCIPAL_NAME, parameters);
        if (!CollectionUtils.isEmpty(authorizationList)) {
            for (OAuth2Authorization oAuth2Authorization : authorizationList) {
                this.oAuth2AuthorizationService.remove(oAuth2Authorization);
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
