package com.zf1976.mayi.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.upms.biz.pojo.dto.menu.MenuDTO;
import com.zf1976.mayi.upms.biz.pojo.query.MenuQueryParam;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.vo.menu.MenuBuildVO;
import com.zf1976.mayi.upms.biz.pojo.vo.menu.MenuVO;
import com.zf1976.mayi.upms.biz.service.SysMenuService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

/**
 * @author mac
 */
@RestController
@RequestMapping("/api/menus")
public class SysMenuController {

    private final SysMenuService service;

    public SysMenuController(SysMenuService service) {
        this.service = service;
    }

    @GetMapping("/build")
    public DataResult<Collection<MenuBuildVO>> buildMenuRoute() {
        return DataResult.success(service.generatedMenu());
    }

    @PostMapping("/page")
    public DataResult<IPage<MenuVO>> selectMenuPage(@RequestBody Query<MenuQueryParam> query) {
        return DataResult.success(service.selectMenuPage(query));
    }

    @PostMapping("/vertex/{id}")
    public DataResult<IPage<MenuVO>> menuVertex(@PathVariable @NotNull Long id) {
        return DataResult.success(service.selectMenuVertex(id));
    }

    @PostMapping("/save")
    public DataResult<Void> saveMenu(@RequestBody @Validated(ValidationInsertGroup.class) MenuDTO dto) {
        return DataResult.success(service.saveMenu(dto));
    }

    @PutMapping("/update")
    public DataResult<Void> updateMenu(@RequestBody @Validated(ValidationUpdateGroup.class) MenuDTO dto) {
        return DataResult.success(service.updateMenu(dto));
    }

    @DeleteMapping("/delete")
    public DataResult<Void> deleteMenu(@RequestBody Set<Long> ids) {
        return DataResult.success(service.deleteMenuList(ids));
    }
}
