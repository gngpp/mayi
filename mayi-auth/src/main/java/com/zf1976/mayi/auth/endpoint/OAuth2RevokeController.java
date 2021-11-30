package com.zf1976.mayi.auth.endpoint;

import com.zf1976.mayi.auth.service.OAuth2RevokeService;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.remote.annotation.CommunicateAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mac
 * 2021/11/28 星期日 11:50 下午
 */
@RestController
@RequestMapping("/oauth2/security/revoke")
public class OAuth2RevokeController {

    private final OAuth2RevokeService oAuth2RevokeService;

    public OAuth2RevokeController(OAuth2RevokeService oAuth2RevokeService) {
        this.oAuth2RevokeService = oAuth2RevokeService;
    }

    @CommunicateAuthorize
    @PostMapping("/{username}")
    public DataResult<Void> revokeByUsername(@PathVariable String username) {
        return DataResult.success(this.oAuth2RevokeService.revokeByUsername(username));
    }
}
