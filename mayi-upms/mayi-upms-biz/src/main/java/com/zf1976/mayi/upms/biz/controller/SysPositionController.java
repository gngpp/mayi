package com.zf1976.mayi.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.upms.biz.pojo.dto.position.PositionDTO;
import com.zf1976.mayi.upms.biz.pojo.query.PositionQueryParam;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.vo.job.PositionVO;
import com.zf1976.mayi.upms.biz.service.SysPositionService;
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
