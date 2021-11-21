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

package com.zf1976.mayi.auth.oauth2.provider;

import com.zf1976.mayi.auth.OAuth2ParameterNamesEnhancer;
import com.zf1976.mayi.auth.oauth2.JwtUtils;
import com.zf1976.mayi.auth.oauth2.OAuth2ResourceOwnerCredentialsAuthenticationToken;
import com.zf1976.mayi.auth.oauth2.OAuth2ResourceOwnerPasswordAuthenticationToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author mac
 * 2021/11/15 星期一 11:35 下午
 */
@SuppressWarnings("DuplicatedCode")
public class OAuth2ResourceOwnerCredentialsAuthenticationProvider implements AuthenticationProvider {

    public static final AuthorizationGrantType PASSWORD_CODE = new AuthorizationGrantType("password_code");
    private static final Logger LOGGER = LogManager.getLogger("OAuth2ResourceOwnerCredentialsAuthenticationProvider");
    private static final StringKeyGenerator DEFAULT_REFRESH_TOKEN_GENERATOR = new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);

    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizationService authorizationService;
    private final JwtEncoder jwtEncoder;
    private final Supplier<String> refreshTokenGenerator = DEFAULT_REFRESH_TOKEN_GENERATOR::generateKey;
    private OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer = (context) -> {
    };
    private ProviderSettings providerSettings;

    /**
     * Constructs an {@code OAuth2ClientCredentialsAuthenticationProvider} using the provided parameters.
     *
     * @param authorizationService the authorization service
     * @param jwtEncoder           the jwt encoder
     */
    public OAuth2ResourceOwnerCredentialsAuthenticationProvider(AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService,
                                                                JwtEncoder jwtEncoder) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(jwtEncoder, "jwtEncoder cannot be null");
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.jwtEncoder = jwtEncoder;
    }

    public final void setJwtCustomizer(OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        Assert.notNull(jwtCustomizer, "jwtCustomizer cannot be null");
        this.jwtCustomizer = jwtCustomizer;
    }

    @Autowired(required = false)
    public void setProviderSettings(ProviderSettings providerSettings) {
        this.providerSettings = providerSettings;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        OAuth2ResourceOwnerCredentialsAuthenticationToken resourceOwnerPasswordAuthentication = (OAuth2ResourceOwnerCredentialsAuthenticationToken) authentication;

        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(resourceOwnerPasswordAuthentication);

        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        if (registeredClient == null || !registeredClient.getAuthorizationGrantTypes().contains(PASSWORD_CODE)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        Map<String, Object> additionalParameters = resourceOwnerPasswordAuthentication.getAdditionalParameters();
        String username = (String) additionalParameters.get(OAuth2ParameterNames.USERNAME);
        String password = (String) additionalParameters.get(OAuth2ParameterNames.PASSWORD);
        String uuid = (String) additionalParameters.get(OAuth2ParameterNamesEnhancer.UUID);
        String securityCode = (String) additionalParameters.get(OAuth2ParameterNamesEnhancer.SECURITY_CODE);

        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            LOGGER.debug("got usernamePasswordAuthenticationToken=" + usernamePasswordAuthenticationToken);

            Authentication usernamePasswordAuthentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            // Default to configured scopes
            Set<String> authorizedScopes = registeredClient.getScopes();
            if (!CollectionUtils.isEmpty(resourceOwnerPasswordAuthentication.getScopes())) {
                Set<String> unauthorizedScopes = resourceOwnerPasswordAuthentication.getScopes().stream()
                                                                                    .filter(requestedScope -> !registeredClient.getScopes().contains(requestedScope))
                                                                                    .collect(Collectors.toSet());
                if (!CollectionUtils.isEmpty(unauthorizedScopes)) {
                    throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
                }

                authorizedScopes = new LinkedHashSet<>(resourceOwnerPasswordAuthentication.getScopes());
            }

            String issuer = this.providerSettings != null ? this.providerSettings.getIssuer() : null;

            JoseHeader.Builder headersBuilder = JwtUtils.headers();
            JwtClaimsSet.Builder claimsBuilder = JwtUtils.accessTokenClaims(
                    registeredClient, issuer, clientPrincipal.getName(), authorizedScopes);

            JwtEncodingContext context = JwtEncodingContext.with(headersBuilder, claimsBuilder)
                                                           .registeredClient(registeredClient)
                                                           .principal(usernamePasswordAuthentication)
                                                           .authorizedScopes(authorizedScopes)
                                                           .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                                                           .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                                                           .authorizationGrant(resourceOwnerPasswordAuthentication)
                                                           .build();

            this.jwtCustomizer.customize(context);

            JoseHeader headers = context.getHeaders().build();
            JwtClaimsSet claims = context.getClaims().build();
            Jwt jwtAccessToken = this.jwtEncoder.encode(headers, claims);

            // Use the scopes after customizing the token
            authorizedScopes = claims.getClaim(OAuth2ParameterNames.SCOPE);

            OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                    jwtAccessToken.getTokenValue(), jwtAccessToken.getIssuedAt(),
                    jwtAccessToken.getExpiresAt(), authorizedScopes);

            OAuth2RefreshToken refreshToken = null;
            if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
                refreshToken = generateRefreshToken(registeredClient.getTokenSettings().getRefreshTokenTimeToLive());
            }

            OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                                                                                  .principalName(clientPrincipal.getName())
                                                                                  .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                                                                                  .token(accessToken,
                                                                                          (metadata) ->
                                                                                                  metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, jwtAccessToken.getClaims()))
                                                                                  .attribute(OAuth2Authorization.AUTHORIZED_SCOPE_ATTRIBUTE_NAME, authorizedScopes)
                                                                                  .attribute(Principal.class.getName(), usernamePasswordAuthentication);

            if (refreshToken != null) {
                authorizationBuilder.refreshToken(refreshToken);
            }

            OAuth2Authorization authorization = authorizationBuilder.build();

            this.authorizationService.save(authorization);

            LOGGER.debug("OAuth2Authorization saved successfully");


            Map<String, Object> tokenAdditionalParameters = new HashMap<>();
            claims.getClaims().forEach((key, value) -> {
                if (!key.equals(OAuth2ParameterNames.SCOPE) &&
                        !key.equals(JwtClaimNames.IAT) &&
                        !key.equals(JwtClaimNames.EXP) &&
                        !key.equals(JwtClaimNames.NBF)) {
                    tokenAdditionalParameters.put(key, value);
                }
            });

            LOGGER.debug("returning OAuth2AccessTokenAuthenticationToken");

            return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken, tokenAdditionalParameters);

        } catch (Exception ex) {
            LOGGER.error("problem in authenticate", ex);
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR), ex);
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        boolean supports = OAuth2ResourceOwnerPasswordAuthenticationToken.class.isAssignableFrom(authentication);
        LOGGER.debug("supports authentication=" + authentication + " returning " + supports);
        return supports;
    }

    private OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {

        OAuth2ClientAuthenticationToken clientPrincipal = null;

        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }

        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }

        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    private OAuth2RefreshToken generateRefreshToken(Duration tokenTimeToLive) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(tokenTimeToLive);
        return new OAuth2RefreshToken(this.refreshTokenGenerator.get(), issuedAt, expiresAt);
    }

}
