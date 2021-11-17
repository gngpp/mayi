package com.zf1976.mayi.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.upms.biz.pojo.dto.dict.DictDetailDTO;
import com.zf1976.mayi.upms.biz.pojo.query.DictDetailQueryParam;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.vo.dict.DictDetailVO;
import com.zf1976.mayi.upms.biz.service.SysDictDetailService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author mac
 * @date 2020/10/23 6:58 下午
 */
@RestController
@RequestMapping("/api/dictionaries/details")
public class SysDictDetailController {

    private final SysDictDetailService service;

    public SysDictDetailController(SysDictDetailService service) {
        this.service = service;
    }

    @PostMapping("/page")
    public DataResult<IPage<DictDetailVO>> selectDictDetailPage(@RequestBody Query<DictDetailQueryParam> query) {
        return DataResult.success(service.selectDictDetailPage(query));
    }

    @PostMapping("/save")
    public DataResult<Void> saveDictDetail(@RequestBody @Validated(ValidationInsertGroup.class) DictDetailDTO dto) {
        return DataResult.success(service.saveDictDetail(dto));
    }

    @PutMapping("/update")
    public DataResult<Void> updateDictDetail(@RequestBody @Validated(ValidationUpdateGroup.class) DictDetailDTO dto) {
        return DataResult.success(service.updateDictDetail(dto));
    }

    @DeleteMapping("/delete/{id}")
    public DataResult<Void> deleteDictDetail(@PathVariable Long id) {
        return DataResult.success(service.deleteDictDetail(id));
    }


}
