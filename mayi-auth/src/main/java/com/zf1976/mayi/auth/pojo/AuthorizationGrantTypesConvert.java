package com.zf1976.mayi.auth.pojo;

import org.springframework.security.oauth2.core.Version;

import java.io.Serializable;

/**
 * Auto-generated: 2021-11-19 19:11:19
 *
 * @author bejson.com (i@bejson.com)
 */
public class AuthorizationGrantTypesConvert implements Serializable {

    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AuthorizationGrantTypesConvert() {
    }

    public AuthorizationGrantTypesConvert(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AuthorizationGrantTypes{" +
                "value='" + value + '\'' +
                '}';
    }
}