package com.zf1976.mayi.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.upms.biz.pojo.dto.dept.DepartmentDTO;
import com.zf1976.mayi.upms.biz.pojo.query.DeptQueryParam;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.vo.dept.DepartmentVO;
import com.zf1976.mayi.upms.biz.service.SysDepartmentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
    public DataResult<IPage<DepartmentVO>> selectDepartmentPage(@RequestBody Query<DeptQueryParam> query) {
        return DataResult.success(service.selectDepartmentPage(query));
    }

    @PostMapping("/vertex/{id}")
    public DataResult<IPage<DepartmentVO>> departmentVertex(@PathVariable Long id) {
        return DataResult.success(service.selectDepartmentVertex(id));
    }

    @PostMapping("/save")
    public DataResult<Void> saveDepartment(@RequestBody @Validated({ValidationInsertGroup.class}) DepartmentDTO dto) {
        return DataResult.success(service.savaDepartment(dto));
    }

    @PutMapping("/update")
    public DataResult<Void> updateDepartment(@RequestBody @Validated(ValidationUpdateGroup.class) DepartmentDTO dto) {
        return DataResult.success(service.updateDepartment(dto));
    }

    @DeleteMapping("/delete")
    public DataResult<Void> deleteDepartmentList(@RequestBody Set<Long> ids) {
        return DataResult.success(service.deleteDepartmentList(ids));
    }

    @PostMapping("/download")
    public DataResult<Void> downloadDepartmentExcel(@RequestBody Query<DeptQueryParam> requestPage,
                                                    HttpServletResponse response) {
        return DataResult.success(service.downloadExcelDept(requestPage, response));
    }
}
