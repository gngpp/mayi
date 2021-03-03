package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.ResultData;
import com.zf1976.ant.common.core.foundation.query.RequestPage;
import com.zf1976.ant.upms.biz.pojo.dto.dept.DepartmentDTO;
import com.zf1976.ant.upms.biz.pojo.query.DeptQueryParam;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import com.zf1976.ant.upms.biz.pojo.vo.dept.DepartmentVO;
import com.zf1976.ant.upms.biz.service.SysDepartmentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.Set;

/**
 * @author mac
 * @date 2020/10/26 7:32 下午
 */
@RestController
@RequestMapping("/api/departments")
public class SysDepartmentController {

    private final SysDepartmentService service;

    public SysDepartmentController(SysDepartmentService service) {
        this.service = service;
    }

    @PostMapping("/page")
//    @Authorize("dept:list")
    public ResultData<IPage<DepartmentVO>> selectDeptPage(@RequestBody RequestPage<DeptQueryParam> requestPage) {
        return ResultData.success(service.selectDeptPage(requestPage));
    }

    @PostMapping("/vertex/{id}")
//    @Authorize("dept:list")
    public ResultData<IPage<DepartmentVO>> deptVertex(@PathVariable Long id) {
        return ResultData.success(service.selectDeptVertex(id));
    }

    @PostMapping("/save")
//    @Authorize("dept:add")
    public ResultData<Optional<Void>> saveDept(@RequestBody @Validated({ValidationInsertGroup.class}) DepartmentDTO dto) {
        return ResultData.success(service.savaDept(dto));
    }

    @PutMapping("/update")
//    @Authorize("dept:edit")
    public ResultData<Optional<Void>> updateDept(@RequestBody @Validated(ValidationUpdateGroup.class) DepartmentDTO dto) {
        return ResultData.success(service.updateDept(dto));
    }

    @DeleteMapping("/delete")
//    @Authorize("dept:del")
    public ResultData<Optional<Void>> deleteDeptList(@RequestBody Set<Long> ids) {
        return ResultData.success(service.deleteDeptList(ids));
    }

    @PostMapping("/download")
//    @Authorize("dept:list")
    public ResultData<Optional<Void>> downloadDeptExcel(@RequestBody RequestPage<DeptQueryParam> requestPage, HttpServletResponse response) {
        return ResultData.success(service.downloadExcelDept(requestPage, response));
    }
}
