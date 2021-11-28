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

package com.zf1976.mayi.common.remote.communication;

import com.zf1976.mayi.common.core.util.IpUtil;
import com.zf1976.mayi.common.core.util.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author mac
 * @date 2021/6/13
 */
@Aspect
@Component
@RefreshScope
public class InnerApiAspect {

    private final DiscoveryClient discoveryClient;

    public InnerApiAspect(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * Inner注解方法内部鉴权
     *
     * @param proceedingJoinPoint 切点
     * @param inner 注解
     * @return {@link Object}
     * @throws Throwable 异常
     */
    @Around("@annotation(Inner) && @annotation(inner)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, Inner inner) throws Throwable {
        if (inner != null) {
            // 判断是否内部实例
            List<String> clientServices = this.discoveryClient.getServices();
            HttpServletRequest request = RequestUtil.getRequest();
            String ipAddress = RequestUtil.getIpAddress();
            boolean isAuthentication = false;
            if (IpUtil.isInnerIp(ipAddress)) {
                isAuthentication = RemoteContext.checkRemoteServerAuthentication(request);
            } else {
                for (String service : clientServices) {
                    ServiceInstance serviceInstance = discoveryClient.getInstances(service).get(0);
                    if (serviceInstance != null &&serviceInstance.getHost().equals(ipAddress)) {
                        isAuthentication = RemoteContext.checkRemoteServerAuthentication(request);
                        if (isAuthentication) {
                            return proceedingJoinPoint.proceed();
                        }
                    }
                }
            }
            if (!isAuthentication) {
                throw new InnerAuthenticationException("Internal service call authentication failed");
            }
        }
        return proceedingJoinPoint.proceed();
    }

}
