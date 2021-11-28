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

package com.zf1976.mayi.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.upms.biz.annotation.Log;
import com.zf1976.mayi.upms.biz.pojo.dto.role.RoleDTO;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.query.RoleQueryParam;
import com.zf1976.mayi.upms.biz.pojo.vo.role.RoleVO;
import com.zf1976.mayi.upms.biz.service.SysRoleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author mac
 * @date 2020/11/22
 **/
@RestController
@RequestMapping("/api/roles")
public class SysRoleController {

    private final SysRoleService service;

    public SysRoleController(SysRoleService service) {
        this.service = service;
    }

    @Log(description = "根据所有角色")
    @GetMapping("/all")
    public DataResult<IPage<RoleVO>> findAll() {
        return DataResult.success(this.service.findAll());
    }

    @Log(description = "分页查询角色")
    @PostMapping("/page")
    public DataResult<IPage<RoleVO>> findByQuery(@RequestBody Query<RoleQueryParam> query) {
        return DataResult.success(service.findByQuery(query));
    }

    @Log(description = "根据id查询角色")
    @PostMapping("/{id}")
    public DataResult<RoleVO> findById(@PathVariable @NotNull Long id) {
        return DataResult.success(service.findById(id));
    }

    @Log(description = "根据id查询角色级别")
    @GetMapping("/level")
    public DataResult<Integer> findByUsernameForLevel(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        return DataResult.success(service.findByUsernameForLevel(principal.getName()));
    }

    @Log(description = "根据id修改角色状态")
    @PatchMapping("/status")
    public DataResult<Void> updateByIdAndEnabled(@RequestParam @NotNull Long id,
                                                 @RequestParam @NotNull Boolean enabled) {
        return DataResult.success(service.updateByIdAndEnabled(id, enabled));
    }

    @Log(description = "新增角色")
    @PostMapping("/save")
    public DataResult<Void> saveOne(@RequestBody @Validated(ValidationInsertGroup.class) RoleDTO dto) {
        return DataResult.success(service.saveOne(dto));
    }

    @Log(description = "更新角色")
    @PutMapping("/update")
    public DataResult<Void> updateOne(@RequestBody @Validated(ValidationUpdateGroup.class) RoleDTO dto) {
        return DataResult.success(service.updateOne(dto));
    }

    @Log(description = "删除角色")
    @DeleteMapping("/delete")
    public DataResult<Void> deleteByIds(@RequestBody @NotNull Set<Long> ids) {
        return DataResult.success(service.deleteByIds(ids));
    }
}
