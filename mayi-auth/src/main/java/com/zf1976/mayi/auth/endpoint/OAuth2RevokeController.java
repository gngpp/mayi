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
