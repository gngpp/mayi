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

package com.zf1976.mayi.upms.biz.security.controller;

import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.upms.biz.pojo.ResourceLinkBinding;
import com.zf1976.mayi.upms.biz.pojo.RoleBinding;
import com.zf1976.mayi.upms.biz.security.service.PermissionBindingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(
        value = "/api/security/permission"
)
public class PermissionBindingController {

   private final PermissionBindingService bindingService;

    public PermissionBindingController(PermissionBindingService bindingService) {
        this.bindingService = bindingService;
    }

    @PostMapping("/binding/role/list")
    @PreAuthorize("hasRole('root')")
    public DataResult<List<RoleBinding>> selectBindingRoleList() {
        return DataResult.success(bindingService.selectRoleBindingList());
    }

    @PostMapping("/binding/resource/list")
    @PreAuthorize("hasRole('root')")
    public DataResult<List<ResourceLinkBinding>> selectBindingResourceList() {
        return DataResult.success(this.bindingService.selectResourceLinkBindingList());
    }

    @PostMapping("/binding/resource")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> bindResource(@NotNull Long id, @RequestBody @NotNull Set<Long> permissionList) {
        return DataResult.success(this.bindingService.bindingResource(id, permissionList));
    }

    @PostMapping("/binding/role")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> bindingRole(@NotNull Long id, @RequestBody @NotNull Set<Long> permissionList) {
        return DataResult.success(this.bindingService.bindingRole(id, permissionList));
    }

    @PutMapping("/unbinding/resource")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> unbindingResource(@NotNull Long id, @RequestBody @NotNull Set<Long> permissionList) {
        return DataResult.success(this.bindingService.unbindingResource(id, permissionList));
    }

    @PutMapping("/unbinding/role")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> unbindingRole(@NotNull Long id, @RequestBody @NotNull Set<Long> permissionList) {
        return DataResult.success(this.bindingService.unbindingRole(id, permissionList));
    }

}
