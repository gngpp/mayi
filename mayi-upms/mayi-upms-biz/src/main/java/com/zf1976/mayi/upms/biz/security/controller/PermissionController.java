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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.upms.biz.pojo.dto.PermissionDTO;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.vo.PermissionVO;
import com.zf1976.mayi.upms.biz.security.service.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author mac
 * @date 2021/5/12
 */
@RestController
@RequestMapping(
        value = "/api/security/permissions"
)
public class PermissionController {

    private final PermissionService service;

    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('root')")
    public DataResult<IPage<PermissionVO>> selectPermissionPage(@RequestBody Query<?> query) {
        return DataResult.success(this.service.selectPermissionByPage(query));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('root')")
    DataResult<Void> savaPermission(@RequestBody @Validated(ValidationInsertGroup.class) PermissionDTO permissionDTO) {
        return DataResult.success(this.service.savePermission(permissionDTO));
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('root')")
    DataResult<Void> editPermission(@RequestBody @Validated(ValidationUpdateGroup.class) PermissionDTO permissionDTO) {
        return DataResult.success(this.service.updatePermission(permissionDTO));
    }

    @DeleteMapping("/del")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> deletePermission(@RequestParam @NotNull Long id) {
        return DataResult.success(this.service.deletePermissionById(id));
    }

    @DeleteMapping("/del/batch")
    @PreAuthorize("hasRole('root')")
    public DataResult<Void> deletePermission(@RequestBody @NotNull Set<Long> ids) {
        return DataResult.success(this.service.deletePermissionByIds(ids));
    }

}
