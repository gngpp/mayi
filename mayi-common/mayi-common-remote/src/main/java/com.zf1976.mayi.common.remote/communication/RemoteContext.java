package com.zf1976.mayi.common.remote.communication;

import com.zf1976.mayi.common.security.constants.SecurityConstants;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mac
 * 2021/11/28 星期日 10:02 下午
 */
@Component
@RefreshScope
public class RemoteContext {

    private static SecurityProperties securityProperties;

    public RemoteContext(SecurityProperties securityProperties) {
        RemoteContext.securityProperties = securityProperties;
    }

    /**
     * confirm remote service certification
     *
     * @param request http request
     */
    public static boolean checkRemoteServerAuthentication(HttpServletRequest request) {
        String header = request.getHeader(SecurityConstants.COMMUNICATION_AUTHORIZATION);
        return ObjectUtils.nullSafeEquals(header, securityProperties.getCommunicationToken());
    }
}
