package com.zf1976.mayi.upms.biz.security.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.upms.biz.pojo.ResourceNode;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mac
 * @date 2021/5/12
 */
@RestController
@RequestMapping(value =
        "/api/security/resource"
)
public class ResourceController {

    private final DynamicDataSourceService dynamicDataSourceService;

    public ResourceController(DynamicDataSourceService dynamicDataSourceService) {
        this.dynamicDataSourceService = dynamicDataSourceService;
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('admin')")
    public DataResult<IPage<ResourceNode>> selectResourceByPage(@RequestBody Query<?> page) {
        return DataResult.success(this.dynamicDataSourceService.selectResourceNodeByPage(page));
    }

}
