package com.zf1976.mayi.auth.pojo;

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
    private boolean requireProofKey;

    @NotNull(message = "requireAuthorizationConsent cannot been null.")
    private boolean requireAuthorizationConsent;

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