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

package com.gngpp.mayi.upms.biz.feign;

import com.gngpp.mayi.common.core.constants.ServiceNameConstants;
import com.gngpp.mayi.common.core.foundation.DataResult;
import com.gngpp.mayi.common.security.constants.SecurityConstants;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author ant
 * Create by Ant on 2021/3/28 5:03 PM
 */
@RefreshScope
@FeignClient(ServiceNameConstants.AUTH_SERVICE)
public interface RemoteAuthClient {


    /**
     * 远程调用认证撤销服务
     *
     * @date 2021-05-07 12:03:42
     * @param communicationToken 通信令牌
     * @return {@link DataResult}
     */
    @PostMapping("/oauth2/security/revoke/{username}")
    @SuppressWarnings("rawtypes")
    DataResult revoke(@RequestHeader(value = SecurityConstants.COMMUNICATION_AUTHORIZATION) String communicationToken,
                      @PathVariable String username);

}
