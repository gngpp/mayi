package com.zf1976.mayi.auth;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

/**
 * @author mac
 * 2021/11/15 星期一 11:53 下午
 */
public interface OAuth2ParameterNamesEnhancer extends OAuth2ParameterNames {

    String SECURITY_CODE = "security_code";

    String UUID = "uuid";

}
