package com.zf1976.mayi.auth.deprecate;

import org.springframework.security.core.userdetails.UserDetails;


/**
 * @author ant
 * Create by Ant on 2020/9/8 9:06 下午
 */
@Deprecated
public class LoginResponse {

    /**
     * 登陆成功返回前端 信息
     */
    private String token;

    /**
     * 用户信息
     */
    private UserDetails user;

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public UserDetails getUser() {
        return user;
    }

    public void setUser(UserDetails user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", user=" + user +
                '}';
    }
}
