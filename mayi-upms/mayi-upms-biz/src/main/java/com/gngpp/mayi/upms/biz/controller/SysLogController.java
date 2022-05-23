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
import com.gngpp.mayi.upms.biz.pojo.query.LogQueryParam;
import com.gngpp.mayi.upms.biz.pojo.query.Query;
import com.gngpp.mayi.upms.biz.pojo.vo.base.AbstractLogVO;
import com.gngpp.mayi.upms.biz.service.SysLogService;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author mac
 * @date 2021/1/25
 **/
@RestController
@RequestMapping("/api/logs")
public class SysLogController {

    private final SysLogService service;

    public SysLogController(SysLogService service) {
        this.service = service;
    }

    @PostMapping("/page")
    public DataResult<IPage<AbstractLogVO>> findByQuery(@RequestBody Query<LogQueryParam> requestPage) {
        return DataResult.success(service.findByQuery(requestPage));
    }

    @PostMapping("/users/page")
    public DataResult<IPage<AbstractLogVO>> findByQueryForUser(@RequestBody Query<LogQueryParam> requestPage) {
        return DataResult.success(service.findByQueryForUser(requestPage));
    }

    @DeleteMapping("/delete")
    public DataResult<Void> deleteByIds(@RequestBody @NotNull Set<Long> ids) {
        return DataResult.success(service.deleteByIds(ids));
    }

    @DeleteMapping("/delete/error")
    public DataResult<Void> deleteError() {
        return DataResult.success(service.deleteError());
    }

    @DeleteMapping("/delete/info")
    public DataResult<Void> deleteInfo() {
        return DataResult.success(service.deleteInfo());
    }
}
