package com.zf1976.mayi.auth.pojo.dto;

import com.zf1976.mayi.auth.pojo.AuthorizationGrantTypesConvert;
import com.zf1976.mayi.auth.pojo.ClientAuthenticationMethodsConvert;
import com.zf1976.mayi.auth.pojo.ClientSettingsConvert;
import com.zf1976.mayi.auth.pojo.TokenSettingsConvert;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import org.springframework.security.oauth2.core.Version;

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
    private Set<ClientAuthenticationMethodsConvert> clientAuthenticationMethods;

    @NotNull(message = "AuthorizationGrantTypesConvert cannot been null.")
    private Set<AuthorizationGrantTypesConvert> authorizationGrantTypes;

    @NotNull(message = "redirectUris cannot been null.")
    private Set<String> redirectUris;

    @NotNull(message = "scopes cannot been null.")
    private Set<String> scopes;

    @NotNull(message = "clientSettings cannot been null.")
    private ClientSettingsConvert clientSettings;

    @NotNull(message = "tokenSettings cannot been null.")
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

    public Set<ClientAuthenticationMethodsConvert> getClientAuthenticationMethods() {
        return clientAuthenticationMethods;
    }

    public RegisteredClientDTO setClientAuthenticationMethods(Set<ClientAuthenticationMethodsConvert> clientAuthenticationMethods) {
        this.clientAuthenticationMethods = clientAuthenticationMethods;
        return this;
    }

    public Set<AuthorizationGrantTypesConvert> getAuthorizationGrantTypes() {
        return authorizationGrantTypes;
    }

    public RegisteredClientDTO setAuthorizationGrantTypes(Set<AuthorizationGrantTypesConvert> authorizationGrantTypes) {
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
