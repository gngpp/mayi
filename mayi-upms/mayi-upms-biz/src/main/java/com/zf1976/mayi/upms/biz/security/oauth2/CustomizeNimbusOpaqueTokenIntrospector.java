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
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.upms.biz.security.oauth2;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.TokenIntrospectionResponse;
import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.*;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 支持获取authorities属性权限
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class CustomizeNimbusOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    private final Log logger = LogFactory.getLog(getClass());

    private Converter<String, RequestEntity<?>> requestEntityConverter;

    private final RestOperations restOperations;

    /**
     * Creates a {@code OpaqueTokenAuthenticationProvider} with the provided parameters
     * @param introspectionUri The introspection endpoint uri
     * @param clientId The client id authorized to introspect
     * @param clientSecret The client's secret
     */
    public CustomizeNimbusOpaqueTokenIntrospector(String introspectionUri, String clientId, String clientSecret) {
        Assert.notNull(introspectionUri, "introspectionUri cannot be null");
        Assert.notNull(clientId, "clientId cannot be null");
        Assert.notNull(clientSecret, "clientSecret cannot be null");
        this.requestEntityConverter = this.defaultRequestEntityConverter(URI.create(introspectionUri));
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(clientId, clientSecret));
        this.restOperations = restTemplate;
    }

    /**
     * Creates a {@code OpaqueTokenAuthenticationProvider} with the provided parameters
     *
     * The given {@link RestOperations} should perform its own client authentication
     * against the introspection endpoint.
     * @param introspectionUri The introspection endpoint uri
     * @param restOperations The client for performing the introspection request
     */
    public CustomizeNimbusOpaqueTokenIntrospector(String introspectionUri, RestOperations restOperations) {
        Assert.notNull(introspectionUri, "introspectionUri cannot be null");
        Assert.notNull(restOperations, "restOperations cannot be null");
        this.requestEntityConverter = this.defaultRequestEntityConverter(URI.create(introspectionUri));
        this.restOperations = restOperations;
    }

    private Converter<String, RequestEntity<?>> defaultRequestEntityConverter(URI introspectionUri) {
        return (token) -> {
            HttpHeaders headers = requestHeaders();
            MultiValueMap<String, String> body = requestBody(token);
            return new RequestEntity<>(body, headers, HttpMethod.POST, introspectionUri);
        };
    }

    private HttpHeaders requestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private MultiValueMap<String, String> requestBody(String token) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", token);
        return body;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        RequestEntity<?> requestEntity = this.requestEntityConverter.convert(token);
        if (requestEntity == null) {
            throw new OAuth2IntrospectionException("requestEntityConverter returned a null entity");
        }
        ResponseEntity<String> responseEntity = makeRequest(requestEntity);
        HTTPResponse httpResponse = adaptToNimbusResponse(responseEntity);
        TokenIntrospectionResponse introspectionResponse = parseNimbusResponse(httpResponse);
        TokenIntrospectionSuccessResponse introspectionSuccessResponse = castToNimbusSuccess(introspectionResponse);
        return convertClaimsSet(token, introspectionSuccessResponse);
    }

    /**
     * Sets the {@link Converter} used for converting the OAuth 2.0 access token to a
     * {@link RequestEntity} representation of the OAuth 2.0 token introspection request.
     * @param requestEntityConverter the {@link Converter} used for converting to a
     * {@link RequestEntity} representation of the token introspection request
     */
    public void setRequestEntityConverter(Converter<String, RequestEntity<?>> requestEntityConverter) {
        Assert.notNull(requestEntityConverter, "requestEntityConverter cannot be null");
        this.requestEntityConverter = requestEntityConverter;
    }

    private ResponseEntity<String> makeRequest(RequestEntity<?> requestEntity) {
        try {
            return this.restOperations.exchange(requestEntity, String.class);
        } catch (Exception ex) {
            throw new OAuth2IntrospectionException(ex.getMessage(), ex);
        }
    }

    private HTTPResponse adaptToNimbusResponse(ResponseEntity<String> responseEntity) {
        HTTPResponse response = new HTTPResponse(responseEntity.getStatusCodeValue());
        Assert.notNull(responseEntity.getHeaders().getContentType(), "response content-type cannot been null!");
        response.setHeader(HttpHeaders.CONTENT_TYPE, responseEntity.getHeaders().getContentType().toString());
        response.setContent(responseEntity.getBody());
        if (response.getStatusCode() != HTTPResponse.SC_OK) {
            throw new OAuth2IntrospectionException("Introspection endpoint responded with " + response.getStatusCode());
        }
        return response;
    }

    private TokenIntrospectionResponse parseNimbusResponse(HTTPResponse response) {
        try {
            return TokenIntrospectionResponse.parse(response);
        } catch (Exception ex) {
            throw new OAuth2IntrospectionException(ex.getMessage(), ex);
        }
    }

    private TokenIntrospectionSuccessResponse castToNimbusSuccess(TokenIntrospectionResponse introspectionResponse) {
        if (!introspectionResponse.indicatesSuccess()) {
            throw new OAuth2IntrospectionException("Token introspection failed");
        }
        return (TokenIntrospectionSuccessResponse) introspectionResponse;
    }

    private OAuth2AuthenticatedPrincipal convertClaimsSet(String token, final TokenIntrospectionSuccessResponse response) {
        // relying solely on the authorization server to validate this token (not checking
        // 'exp', for example)
        if (!response.isActive()) {
            this.logger.trace("Did not validate token since it is inactive");
            throw new BadOpaqueTokenException("Provided token isn't active");
        }
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        try {
            JSONArray jsonArray = (JSONArray) JWTParser.parse(token)
                                                       .getJWTClaimsSet()
                                                       .getClaims()
                                                       .get("authorities");
            if (jsonArray != null) {
                Set<SimpleGrantedAuthority> simpleGrantedAuthoritySet = jsonArray.stream()
                                                                                 .map(Object::toString)
                                                                                 .map(SimpleGrantedAuthority::new)
                                                                                 .collect(Collectors.toSet());
                authorities.addAll(simpleGrantedAuthoritySet);
            }
        } catch (ParseException e) {
            logger.debug(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        Map<String, Object> claims = response.toJSONObject();
        if (response.getAudience() != null) {
            List<String> audiences = new ArrayList<>();
            for (Audience audience : response.getAudience()) {
                audiences.add(audience.getValue());
            }
            claims.put(OAuth2IntrospectionClaimNames.AUDIENCE, Collections.unmodifiableList(audiences));
        }
        if (response.getClientID() != null) {
            claims.put(OAuth2IntrospectionClaimNames.CLIENT_ID, response.getClientID().getValue());
        }
        if (response.getExpirationTime() != null) {
            Instant exp = response.getExpirationTime().toInstant();
            claims.put(OAuth2IntrospectionClaimNames.EXPIRES_AT, exp);
        }
        if (response.getIssueTime() != null) {
            Instant iat = response.getIssueTime().toInstant();
            claims.put(OAuth2IntrospectionClaimNames.ISSUED_AT, iat);
        }
        if (response.getIssuer() != null) {
            claims.put(OAuth2IntrospectionClaimNames.ISSUER, issuer(response.getIssuer().getValue()));
        }
        if (response.getNotBeforeTime() != null) {
            claims.put(OAuth2IntrospectionClaimNames.NOT_BEFORE, response.getNotBeforeTime().toInstant());
        }
        if (response.getScope() != null) {
            List<String> scopes = Collections.unmodifiableList(response.getScope().toStringList());
            claims.put(OAuth2IntrospectionClaimNames.SCOPE, scopes);
            for (String scope : scopes) {
                String authorityPrefix = "";
                authorities.add(new SimpleGrantedAuthority(authorityPrefix + scope));
            }
        }
        return new OAuth2IntrospectionAuthenticatedPrincipal(claims, authorities);
    }

    private URL issuer(String uri) {
        try {
            return new URL(uri);
        } catch (Exception ex) {
            throw new OAuth2IntrospectionException(
                    "Invalid " + OAuth2IntrospectionClaimNames.ISSUER + " value: " + uri);
        }
    }
}
