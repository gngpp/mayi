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

package com.gngpp.mayi.upms.biz.security.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gngpp.mayi.common.core.foundation.DataResult;
import com.gngpp.mayi.upms.biz.pojo.ResourceNode;
import com.gngpp.mayi.upms.biz.pojo.query.Query;
import com.gngpp.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author mac
 * @date 2021/5/12
 */
@RestController
@RequestMapping(value =
        "/api/security/resources"
)
public class ResourceController {

    private final DynamicDataSourceService dynamicDataSourceService;

    public ResourceController(DynamicDataSourceService dynamicDataSourceService) {
        this.dynamicDataSourceService = dynamicDataSourceService;
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('root')")
    public DataResult<IPage<ResourceNode>> findByQuery(@RequestBody Query<?> query) {
        return DataResult.success(this.dynamicDataSourceService.findByQuery(query));
    }

    @PatchMapping("/update/enabled/{id}")
    @PreAuthorize("hasRole('root')")
    public DataResult<IPage<ResourceNode>> updateEnabledById(@PathVariable @NotNull Long id, @RequestParam @NotNull Boolean enabled) {
        return DataResult.success(this.dynamicDataSourceService.updateEnabledById(id, enabled));
    }

    @PatchMapping("/update/allow/{id}")
    @PreAuthorize("hasRole('root')")
    public DataResult<IPage<ResourceNode>> updateAllowById(@PathVariable @NotNull Long id, @RequestParam @NotNull Boolean allow) {
        return DataResult.success(this.dynamicDataSourceService.updateAllowById(id, allow));
    }

}
