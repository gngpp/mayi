package com.zf1976.mayi.auth.service;

/**
 * @author mac
 * 2021/11/28 星期日 11:20 下午
 */
public interface OAuth2RevokeService {


    Void revokeByUsername(String username);

}
