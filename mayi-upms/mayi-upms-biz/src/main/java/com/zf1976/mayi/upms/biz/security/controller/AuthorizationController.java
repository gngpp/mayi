package com.zf1976.mayi.upms.biz.security.controller;

import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.upms.biz.communication.Inner;
import com.zf1976.mayi.upms.biz.pojo.User;
import com.zf1976.mayi.upms.biz.service.SysUserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author mac
 * @date 2021/6/13
 */
@RestController
@RequestMapping("/api/authorities/user")
public class AuthorizationController {

    private final SysUserService userService;

    public AuthorizationController(SysUserService userService) {
        this.userService = userService;
    }

    @Inner
    @PostMapping("/{username}")
    public DataResult<User> getUser(@PathVariable("username")String username) {
        return DataResult.success(this.userService.findByUsername(username));
    }
}
