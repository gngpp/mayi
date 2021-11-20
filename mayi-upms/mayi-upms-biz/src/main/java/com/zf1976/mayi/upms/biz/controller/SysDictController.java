package com.zf1976.mayi.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.upms.biz.pojo.dto.dict.DictDTO;
import com.zf1976.mayi.upms.biz.pojo.query.DictQueryParam;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.vo.dict.DictVO;
import com.zf1976.mayi.upms.biz.service.SysDictService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * @author mac
 * @date 2020/10/22 9:14 下午
 */
@RestController
@RequestMapping("/api/dictionaries")
public class SysDictController {

    private final SysDictService service;

    public SysDictController(SysDictService service) {
        this.service = service;
    }

    @PostMapping("/page")
    public DataResult<IPage<DictVO>> findByQuery(@RequestBody Query<DictQueryParam> query) {
        return DataResult.success(service.findByQuery(query));
    }

    @PostMapping("/save")
    public DataResult<Void> saveOne(@RequestBody @Validated({ValidationInsertGroup.class}) DictDTO dto) {
        return DataResult.success(service.saveOne(dto));
    }

    @PutMapping("/update")
    public DataResult<Void> updateOne(@RequestBody @Validated(ValidationUpdateGroup.class) DictDTO dto) {
        return DataResult.success(service.updateOne(dto));
    }

    @DeleteMapping("/delete")
    public DataResult<Void> deleteByIds(@RequestBody Set<Long> ids) {
        return DataResult.success(service.deleteByIds(ids));
    }

    @PostMapping("/download")
    public DataResult<Void> downloadExcel(@RequestBody Query<DictQueryParam> requestPage, HttpServletResponse response) {
        return DataResult.success(service.downloadExcel(requestPage, response));
    }

}
