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
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.upms.biz.feign;

import com.zf1976.mayi.common.security.constants.ServiceNameConstants;
import com.zf1976.mayi.common.core.foundation.DataResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author ant
 * Create by Ant on 2021/3/28 5:03 PM
 */
@FeignClient(ServiceNameConstants.AUTH_SERVICE)
public interface RemoteAuthClient {

    /**
     * 远程调用服务登出处理
     *
     * @date 2021-05-07 12:03:42
     * @param token 令牌
     * @return {@link DataResult}
     */
    @PostMapping("/oauth/logout")
    @SuppressWarnings("rawtypes")
    DataResult logout(@RequestHeader(value = "Authorization") String token);

}
