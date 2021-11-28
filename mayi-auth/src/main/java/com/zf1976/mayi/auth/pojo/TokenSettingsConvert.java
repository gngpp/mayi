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
package com.zf1976.mayi.auth.pojo;

import org.springframework.security.oauth2.core.Version;

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