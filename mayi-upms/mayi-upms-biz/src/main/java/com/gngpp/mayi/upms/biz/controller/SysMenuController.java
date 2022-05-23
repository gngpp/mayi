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

package com.gngpp.mayi.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gngpp.mayi.common.core.foundation.DataResult;
import com.gngpp.mayi.common.core.validate.ValidationInsertGroup;
import com.gngpp.mayi.common.core.validate.ValidationUpdateGroup;
import com.gngpp.mayi.upms.biz.pojo.dto.menu.MenuDTO;
import com.gngpp.mayi.upms.biz.pojo.query.MenuQueryParam;
import com.gngpp.mayi.upms.biz.pojo.query.Query;
import com.gngpp.mayi.upms.biz.pojo.vo.menu.MenuBuildVO;
import com.gngpp.mayi.upms.biz.pojo.vo.menu.MenuVO;
import com.gngpp.mayi.upms.biz.service.SysMenuService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

/**
 * @author mac
 */
@RestController
@RequestMapping("/api/menus")
public class SysMenuController {

    private final SysMenuService service;

    public SysMenuController(SysMenuService service) {
        this.service = service;
    }

    @GetMapping("/build")
    public DataResult<Collection<MenuBuildVO>> findAll() {
        return DataResult.success(service.generatedMenu());
    }

    @PostMapping("/page")
    public DataResult<IPage<MenuVO>> findByQuery(@RequestBody Query<MenuQueryParam> query) {
        return DataResult.success(service.findByQuery(query));
    }

    @PostMapping("/vertex/{id}")
    public DataResult<IPage<MenuVO>> findVertexById(@PathVariable @NotNull Long id) {
        return DataResult.success(service.findVertexById(id));
    }

    @PostMapping("/save")
    public DataResult<Void> saveOne(@RequestBody @Validated(ValidationInsertGroup.class) MenuDTO dto) {
        return DataResult.success(service.saveOne(dto));
    }

    @PutMapping("/update")
    public DataResult<Void> updateOne(@RequestBody @Validated(ValidationUpdateGroup.class) MenuDTO dto) {
        return DataResult.success(service.updateOne(dto));
    }

    @DeleteMapping("/delete")
    public DataResult<Void> deleteByIds(@RequestBody @NotNull Set<Long> ids) {
        return DataResult.success(service.deleteByIds(ids));
    }
}
