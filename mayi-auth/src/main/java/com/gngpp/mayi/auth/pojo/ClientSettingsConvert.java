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

package com.gngpp.mayi.auth.pojo;

import org.springframework.security.oauth2.core.Version;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Auto-generated: 2021-11-19 19:11:19
 *
 * @author bejson.com (i@bejson.com)
 */
public class ClientSettingsConvert implements Serializable {

    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @NotNull(message = "requireProofKey cannot been null.")
    private Boolean requireProofKey;

    @NotNull(message = "requireAuthorizationConsent cannot been null.")
    private Boolean requireAuthorizationConsent;

    public ClientSettingsConvert() {
    }

    public ClientSettingsConvert(boolean requireProofKey, boolean requireAuthorizationConsent) {
        this.requireProofKey = requireProofKey;
        this.requireAuthorizationConsent = requireAuthorizationConsent;
    }

    public void setRequireProofKey(boolean requireProofKey) {
        this.requireProofKey = requireProofKey;
    }

    public boolean getRequireProofKey() {
         return requireProofKey;
     }

    public void setRequireAuthorizationConsent(boolean requireAuthorizationConsent) {
        this.requireAuthorizationConsent = requireAuthorizationConsent;
    }

    public boolean getRequireAuthorizationConsent() {
        return requireAuthorizationConsent;
    }

    @Override
    public String toString() {
        return "ClientSettings{" +
                "requireProofKey=" + requireProofKey +
                ", requireAuthorizationConsent=" + requireAuthorizationConsent +
                '}';
    }
}