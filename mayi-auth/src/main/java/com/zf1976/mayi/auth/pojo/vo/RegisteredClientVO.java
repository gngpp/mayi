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

package com.zf1976.mayi.auth.pojo.vo;
import com.zf1976.mayi.auth.pojo.ClientSettingsConvert;
import com.zf1976.mayi.auth.pojo.TokenSettingsConvert;
import org.springframework.security.oauth2.core.Version;

import java.io.Serializable;
import java.util.Set;

/**
 * Auto-generated: 2021-11-19 19:11:19
 *
 * @author bejson.com (i@bejson.com)
 */
public class RegisteredClientVO implements Serializable {

    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;
    private String id;
    private String clientId;
    private Long clientIdIssuedAt;
    private Long clientSecretExpiresAt;
    private String clientSecret;
    private String clientName;
    private Set<String> clientAuthenticationMethods;
    private Set<String> authorizationGrantTypes;
    private Set<String> redirectUris;
    private Set<String> scopes;
    private ClientSettingsConvert clientSettings;
    private TokenSettingsConvert tokenSettings;

    public String getId() {
        return id;
    }

    public Long getClientSecretExpiresAt() {
        return clientSecretExpiresAt;
    }

    public RegisteredClientVO setClientSecretExpiresAt(Long clientSecretExpiresAt) {
        this.clientSecretExpiresAt = clientSecretExpiresAt;
        return this;
    }

    public RegisteredClientVO setId(String id) {
        this.id = id;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public RegisteredClientVO setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public Long getClientIdIssuedAt() {
        return clientIdIssuedAt;
    }

    public RegisteredClientVO setClientIdIssuedAt(Long clientIdIssuedAt) {
        this.clientIdIssuedAt = clientIdIssuedAt;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public RegisteredClientVO setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public String getClientName() {
        return clientName;
    }

    public RegisteredClientVO setClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    public Set<String> getClientAuthenticationMethods() {
        return clientAuthenticationMethods;
    }

    public RegisteredClientVO setClientAuthenticationMethods(Set<String> clientAuthenticationMethods) {
        this.clientAuthenticationMethods = clientAuthenticationMethods;
        return this;
    }

    public Set<String> getAuthorizationGrantTypes() {
        return authorizationGrantTypes;
    }

    public RegisteredClientVO setAuthorizationGrantTypes(Set<String> authorizationGrantTypes) {
        this.authorizationGrantTypes = authorizationGrantTypes;
        return this;
    }

    public Set<String> getRedirectUris() {
        return redirectUris;
    }

    public RegisteredClientVO setRedirectUris(Set<String> redirectUris) {
        this.redirectUris = redirectUris;
        return this;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public RegisteredClientVO setScopes(Set<String> scopes) {
        this.scopes = scopes;
        return this;
    }

    public ClientSettingsConvert getClientSettings() {
        return clientSettings;
    }

    public RegisteredClientVO setClientSettings(ClientSettingsConvert clientSettings) {
        this.clientSettings = clientSettings;
        return this;
    }

    public TokenSettingsConvert getTokenSettings() {
        return tokenSettings;
    }

    public RegisteredClientVO setTokenSettings(TokenSettingsConvert tokenSettings) {
        this.tokenSettings = tokenSettings;
        return this;
    }

    @Override
    public String toString() {
        return "RegisteredClientVO{" +
                "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientIdIssuedAt=" + clientIdIssuedAt +
                ", clientSecretExpiresAt=" + clientSecretExpiresAt +
                ", clientSecret='" + clientSecret + '\'' +
                ", clientName='" + clientName + '\'' +
                ", clientAuthenticationMethods=" + clientAuthenticationMethods +
                ", authorizationGrantTypes=" + authorizationGrantTypes +
                ", redirectUris=" + redirectUris +
                ", scopes=" + scopes +
                ", clientSettings=" + clientSettings +
                ", tokenSettings=" + tokenSettings +
                '}';
    }
}