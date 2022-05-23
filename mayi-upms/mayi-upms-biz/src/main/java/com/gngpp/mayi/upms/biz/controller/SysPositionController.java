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
import com.gngpp.mayi.upms.biz.pojo.dto.position.PositionDTO;
import com.gngpp.mayi.upms.biz.pojo.query.PositionQueryParam;
import com.gngpp.mayi.upms.biz.pojo.query.Query;
import com.gngpp.mayi.upms.biz.pojo.vo.job.PositionVO;
import com.gngpp.mayi.upms.biz.service.SysPositionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author mac
 * @date 2020/10/25 5:40 下午
 */
@RestController
@RequestMapping("/api/positions")
public class SysPositionController {

    private final SysPositionService service;

    public SysPositionController(SysPositionService service) {
        this.service = service;
    }

    @PostMapping("/page")
    public DataResult<IPage<PositionVO>> findByQuery(@RequestBody Query<PositionQueryParam> query) {
        return DataResult.success(service.findByQuery(query));
    }

    @PostMapping("/save")
    public DataResult<Void> saveOne(@RequestBody @Validated(ValidationInsertGroup.class) PositionDTO dto) {
        return DataResult.success(service.saveOne(dto));
    }

    @PutMapping("/update")
    public DataResult<Void> updateOne(@RequestBody @Validated(ValidationUpdateGroup.class) PositionDTO dto) {
        return DataResult.success(service.updateOne(dto));
    }

    @DeleteMapping("/delete")
    public DataResult<Void> deleteByIds(@RequestBody @NotNull Set<Long> ids) {
        return DataResult.success(service.deleteByIds(ids));
    }

    @PostMapping("/download")
    public DataResult<Void> downloadExcel(@RequestBody Query<PositionQueryParam> requestPage,
                                          HttpServletResponse response) {
        return DataResult.success(service.downloadExcel(requestPage, response));
    }
}
