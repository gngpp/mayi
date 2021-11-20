/**
  * Copyright 2021 bejson.com 
  */
package com.zf1976.mayi.auth.pojo;

import org.springframework.security.oauth2.core.Version;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Auto-generated: 2021-11-19 19:11:19
 *
 * @author bejson.com (i@bejson.com)
 */
public class TokenSettingsConvert implements Serializable {

    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @NotNull(message = "refreshTokenTimeToLive cannot been null.")
    private Long refreshTokenTimeToLive;

    @NotNull(message = "reuseRefreshTokens cannot been null.")
    private Boolean reuseRefreshTokens;

    @NotNull(message = "accessTokenTimeToLive cannot been null.")
    private Long accessTokenTimeToLive;

    @NotNull(message = "idTokenSignatureAlgorithm cannot been null.")
    private String idTokenSignatureAlgorithm;

    public TokenSettingsConvert() {
    }

    public Long getRefreshTokenTimeToLive() {
        return refreshTokenTimeToLive;
    }

    public TokenSettingsConvert setRefreshTokenTimeToLive(Long refreshTokenTimeToLive) {
        this.refreshTokenTimeToLive = refreshTokenTimeToLive;
        return this;
    }

    public boolean isReuseRefreshTokens() {
        return reuseRefreshTokens;
    }

    public TokenSettingsConvert setReuseRefreshTokens(boolean reuseRefreshTokens) {
        this.reuseRefreshTokens = reuseRefreshTokens;
        return this;
    }

    public Long getAccessTokenTimeToLive() {
        return accessTokenTimeToLive;
    }

    public TokenSettingsConvert setAccessTokenTimeToLive(Long accessTokenTimeToLive) {
        this.accessTokenTimeToLive = accessTokenTimeToLive;
        return this;
    }

    public String getIdTokenSignatureAlgorithm() {
        return idTokenSignatureAlgorithm;
    }

    public TokenSettingsConvert setIdTokenSignatureAlgorithm(String idTokenSignatureAlgorithm) {
        this.idTokenSignatureAlgorithm = idTokenSignatureAlgorithm;
        return this;
    }

    @Override
    public String toString() {
        return "TokenSettings{" +
                "refreshTokenTimeToLive='" + refreshTokenTimeToLive + '\'' +
                ", reuseRefreshTokens=" + reuseRefreshTokens +
                ", accessTokenTimeToLive='" + accessTokenTimeToLive + '\'' +
                ", idTokenSignatureAlgorithm='" + idTokenSignatureAlgorithm + '\'' +
                '}';
    }

}