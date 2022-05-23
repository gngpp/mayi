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

package com.gngpp.mayi.common.remote.communication;

import com.gngpp.mayi.common.security.constants.SecurityConstants;
import com.gngpp.mayi.common.security.property.SecurityProperties;
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
