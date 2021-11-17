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
    public DataResult<IPage<DictVO>> selectDictPage(@RequestBody Query<DictQueryParam> query) {
        return DataResult.success(service.selectDictPage(query));
    }

    @PostMapping("/save")
    public DataResult<Void> saveDict(@RequestBody @Validated({ValidationInsertGroup.class}) DictDTO dto) {
        return DataResult.success(service.saveDict(dto));
    }

    @PutMapping("/update")
    public DataResult<Void> updateDict(@RequestBody @Validated(ValidationUpdateGroup.class) DictDTO dto) {
        return DataResult.success(service.updateDict(dto));
    }

    @DeleteMapping("/delete")
    public DataResult<Void> deleteDictList(@RequestBody Set<Long> ids) {
        return DataResult.success(service.deleteDictList(ids));
    }

    @PostMapping("/download")
    public DataResult<Void> downloadDictExcel(@RequestBody Query<DictQueryParam> requestPage, HttpServletResponse response) {
        return DataResult.success(service.downloadDictExcel(requestPage, response));
    }

}
