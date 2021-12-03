/*
 *
 *  * Copyright (c) 2021 zf1976
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

package com.zf1976.mayi.upms.biz.security;

import com.zf1976.mayi.common.security.constants.SecurityConstants;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.upms.biz.feign.RemoteAuthClient;
import com.zf1976.mayi.upms.biz.security.exception.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;

@Component
@RefreshScope
public class Context extends SecurityContextHolder {

    private static final Logger log = LoggerFactory.getLogger("[Context]");
    private static SecurityProperties securityProperties;
    private static RemoteAuthClient remoteAuthClient;

    @Autowired
    public void setSecurityProperties(SecurityProperties securityProperties, RemoteAuthClient remoteAuthClient) {
        Context.securityProperties = securityProperties;
        Context.remoteAuthClient = remoteAuthClient;
    }

   public static boolean isOwner() {
       String name = username();
       return isOwner(name);
   }

    public static boolean isOwner(String username) {
        return ObjectUtils.nullSafeEquals(username, securityProperties.getOwner());
    }

   public static String username() {
       return getContext().getAuthentication().getName();
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

    /**
     * revoke user authentication
     *
     * @param username username
     */
   public static void revokeAuthentication(String username) {
        if (isOwner(username)) {
            throw new SecurityException("It is not allowed to revoke root authentication.");
        }
       try {
           remoteAuthClient.revoke(securityProperties.getCommunicationToken(), username);
       } catch (Exception e) {
           log.error(e.getMessage(), e);
           throw new SecurityException("system error");
       }
   }

   public static void revokeAuthentication() {
       revokeAuthentication(username());
   }
}
