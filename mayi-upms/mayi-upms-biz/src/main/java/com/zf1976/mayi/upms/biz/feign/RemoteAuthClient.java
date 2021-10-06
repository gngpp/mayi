package com.zf1976.mayi.upms.biz.feign;

import com.zf1976.mayi.common.core.constants.ServiceNameConstants;
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
