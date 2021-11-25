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

package com.zf1976.mayi.auth.pojo.dto;

import com.zf1976.mayi.auth.pojo.ClientSettingsConvert;
import com.zf1976.mayi.auth.pojo.TokenSettingsConvert;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import org.springframework.security.oauth2.core.Version;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Set;

public class RegisteredClientDTO implements Serializable {

    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @NotNull(groups = ValidationUpdateGroup.class, message = "id cannot been null.")
    @Null(groups = ValidationInsertGroup.class, message = "id must be null.")
    private String id;

    @NotNull(message = "clientId cannot been null.")
    private String clientId;

    @NotNull(message = "clientSecretExpiresAt cannot been null.")
    private Long clientSecretExpiresAt;

    @NotNull(message = "clientSecret cannot been null.")
    private String clientSecret;

    @NotNull(message = "clientName cannot been null.")
    private String clientName;

    @NotNull(message = "ClientAuthenticationMethodsConvert cannot been null.")
    private Set<String> clientAuthenticationMethods;

    @NotNull(message = "AuthorizationGrantTypesConvert cannot been null.")
    private Set<String> authorizationGrantTypes;

    @NotNull(message = "redirectUris cannot been null.")
    private Set<String> redirectUris;

    @NotNull(message = "scopes cannot been null.")
    private Set<String> scopes;

    @NotNull(message = "clientSettings cannot been null.")
    @Valid
    private ClientSettingsConvert clientSettings;

    @NotNull(message = "tokenSettings cannot been null.")
    @Valid
    private TokenSettingsConvert tokenSettings;

    public RegisteredClientDTO() {
    }

    public String getId() {
        return id;
    }

    public RegisteredClientDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public RegisteredClientDTO setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public Long getClientSecretExpiresAt() {
        return clientSecretExpiresAt;
    }

    public RegisteredClientDTO setClientSecretExpiresAt(Long clientSecretExpiresAt) {
        this.clientSecretExpiresAt = clientSecretExpiresAt;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public RegisteredClientDTO setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public String getClientName() {
        return clientName;
    }

    public RegisteredClientDTO setClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    public Set<String> getClientAuthenticationMethods() {
        return clientAuthenticationMethods;
    }

    public RegisteredClientDTO setClientAuthenticationMethods(Set<String> clientAuthenticationMethods) {
        this.clientAuthenticationMethods = clientAuthenticationMethods;
        return this;
    }

    public Set<String> getAuthorizationGrantTypes() {
        return authorizationGrantTypes;
    }

    public RegisteredClientDTO setAuthorizationGrantTypes(Set<String> authorizationGrantTypes) {
        this.authorizationGrantTypes = authorizationGrantTypes;
        return this;
    }

    public Set<String> getRedirectUris() {
        return redirectUris;
    }

    public RegisteredClientDTO setRedirectUris(Set<String> redirectUris) {
        this.redirectUris = redirectUris;
        return this;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public RegisteredClientDTO setScopes(Set<String> scopes) {
        this.scopes = scopes;
        return this;
    }

    public ClientSettingsConvert getClientSettings() {
        return clientSettings;
    }

    public RegisteredClientDTO setClientSettings(ClientSettingsConvert clientSettings) {
        this.clientSettings = clientSettings;
        return this;
    }

    public TokenSettingsConvert getTokenSettings() {
        return tokenSettings;
    }

    public RegisteredClientDTO setTokenSettings(TokenSettingsConvert tokenSettings) {
        this.tokenSettings = tokenSettings;
        return this;
    }

    @Override
    public String toString() {
        return "RegisteredClientDTO{" +
                "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
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
