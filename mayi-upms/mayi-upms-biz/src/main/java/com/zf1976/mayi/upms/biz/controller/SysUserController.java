package com.zf1976.mayi.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.upms.biz.annotation.Log;
import com.zf1976.mayi.upms.biz.mail.ValidateEmailService;
import com.zf1976.mayi.upms.biz.pojo.User;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UpdateEmailDTO;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UpdateInfoDTO;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UpdatePasswordDTO;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UserDTO;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.query.UserQueryParam;
import com.zf1976.mayi.upms.biz.pojo.vo.user.UserVO;
import com.zf1976.mayi.upms.biz.service.SysUserService;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author mac
 */
@RestController
@RequestMapping("/api/users")
@DependsOn("ValidateServiceImpl")
public class SysUserController {

    private final SysUserService service;
    private final ValidateEmailService validateService;
    public SysUserController(SysUserService service) {
        this.service = service;
        this.validateService = ValidateEmailService.validateEmailService();
    }

    @Log(description = "分页查询用户")
    @PostMapping("/page")
    public DataResult<IPage<UserVO>> findByQuery(@RequestBody Query<UserQueryParam> query,
                                                 @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        return DataResult.success(service.findByQuery(query, principal.getName()));
    }

    @Log(description = "添加用户")
    @PostMapping("/save")
    public DataResult<Void> saveOne(@RequestBody @Validated(ValidationInsertGroup.class) UserDTO dto) {
        return DataResult.success(service.saveOne(dto));
    }

    @Log(description = "更新用户")
    @PutMapping("/update")
    public DataResult<Void> updateOne(@RequestBody @Validated(ValidationUpdateGroup.class) UserDTO dto) {
        return DataResult.success(service.updateOne(dto));
    }

    @Log(description = "删除用户")
    @DeleteMapping("/delete")
    public DataResult<Void> deleteByIds(@RequestBody Set<Long> ids) {
        return DataResult.success(service.deleteByIds(ids));
    }

    @Log(description = "获取用户职位")
    @PostMapping("/position/{id}")
    public DataResult<Set<Long>> findPositionById(@PathVariable Long id) {
        return DataResult.success(service.findPositionById(id));
    }

    @Log(description = "获取用户角色")
    @PostMapping("/role/{id}")
    public DataResult<Set<Long>> findRoleById(@PathVariable Long id) {
        return DataResult.success(service.findRoleById(id));
    }

    @Log(description = "修改用户状态")
    @PatchMapping("/update/status")
    public DataResult<Void> updateByIdAndEnabled(@RequestParam @NotNull Long id, @RequestParam @NotNull Boolean enabled) {
        return DataResult.success(service.updateByIdAndEnabled(id, enabled));
    }

    @PostMapping("/update/avatar")
    public DataResult<Void> updateAvatar(@RequestParam("avatar") MultipartFile multipartFile) {
        return DataResult.success(service.updateAvatar(multipartFile));
    }

    @PatchMapping("/update/password")
    public DataResult<Void> updatePassword(@RequestBody @Validated UpdatePasswordDTO dto) {
        return DataResult.success(service.updatePassword(dto));
    }

    @PatchMapping("/update/email/{code}")
    public DataResult<Void> updateEmail(@PathVariable String code, @RequestBody @Validated UpdateEmailDTO dto) {
        return DataResult.success(service.updateEmail(code, dto));
    }

    @GetMapping("/email/reset")
    public DataResult<Void> sendEmailVerifyCode(@RequestParam String email) {
        return DataResult.success(this.validateService.sendVerifyCode(email));
    }

    @PatchMapping("/update/info")
    public DataResult<Void> updateInfo(@RequestBody @Validated UpdateInfoDTO dto) {
        return DataResult.success(service.updateInfo(dto));
    }

    @PostMapping("/info")
    public DataResult<User> findByUsername(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        return DataResult.success(this.service.findByUsername(principal.getName()));
    }
}
