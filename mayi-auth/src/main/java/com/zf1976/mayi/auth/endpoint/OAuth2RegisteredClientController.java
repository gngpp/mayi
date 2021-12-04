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

import com.zf1976.mayi.auth.oauth2.repository.Page;
import com.zf1976.mayi.auth.pojo.dto.RegisteredClientDTO;
import com.zf1976.mayi.auth.pojo.vo.RegisteredClientVO;
import com.zf1976.mayi.auth.service.OAuth2RegisteredClientService;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
@RequestMapping("/oauth2/security/clients")
public class OAuth2RegisteredClientController {

    private final OAuth2RegisteredClientService oAuth2RegisteredClientService;


    public OAuth2RegisteredClientController(OAuth2RegisteredClientService oAuth2RegisteredClientService) {
        this.oAuth2RegisteredClientService = oAuth2RegisteredClientService;
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('root')")
    public DataResult<Page<RegisteredClientVO>> findByPage(@RequestBody @NotNull Page<?> page) {
        return DataResult.success(this.oAuth2RegisteredClientService.findList(page));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> sava(@RequestBody @Validated(ValidationInsertGroup.class) RegisteredClientDTO dto) {
        return DataResult.success(this.oAuth2RegisteredClientService.sava(dto));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> update(@RequestBody @Validated(ValidationUpdateGroup.class) RegisteredClientDTO dto) {
        return DataResult.success(this.oAuth2RegisteredClientService.sava(dto));
    }

    @DeleteMapping("/delete/id/{id}")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> deleteById(@PathVariable String id) {
        return DataResult.success(this.oAuth2RegisteredClientService.deleteById(id));
    }

    @DeleteMapping("/delete/id")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> deleteByIds(@RequestBody Set<String> ids) {
        return DataResult.success(this.oAuth2RegisteredClientService.deleteByIds(ids));
    }

    @DeleteMapping("/delete/client_id/{clientId}")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> deleteByClientId(@PathVariable String clientId) {
        return DataResult.success(this.oAuth2RegisteredClientService.deleteByClientId(clientId));
    }

    @DeleteMapping("/delete/client_id")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> deleteByClientIds(@RequestBody Set<String> clientIds) {
        return DataResult.success(this.oAuth2RegisteredClientService.deleteByClientIds(clientIds));
    }

    @GetMapping("/alg")
    @PreAuthorize("hasRole('root')")
    public DataResult<Set<String>> loadTokenSignatureAlgorithm() {
        return DataResult.success(this.oAuth2RegisteredClientService.loadTokenSignatureAlgorithm());
    }

    @GetMapping("/grants")
    public DataResult<Set<String>> loadAuthorizationGrantTypes() {
        return DataResult.success(this.oAuth2RegisteredClientService.loadAuthorizationGrantTypes());
    }

    @GetMapping("/authentication_methods")
    public DataResult<Set<String>> loadClientAuthenticationMethods() {
        return DataResult.success(this.oAuth2RegisteredClientService.loadClientAuthenticationMethods());
    }

}
