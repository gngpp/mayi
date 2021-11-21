/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.auth.config.evaluator;

import com.zf1976.mayi.auth.Context;
import com.zf1976.mayi.auth.JwtTokenProvider;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 存在原生hasAuthority不需要再添加次评估，否则将校验两次
 * @author ant
 * Create by Ant on 2020/10/14 8:03 下午
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public class SecurityPermissionEvaluator implements PermissionEvaluator {

    public boolean hasPrivilege(Authentication authentication, String... permission) {
        if (Context.isOwner()) {
            return true;
        } else {
            return AuthorityUtils.authorityListToSet(authentication.getAuthorities())
                                 .containsAll(Arrays.asList(permission));
        }

    }

    /**
     * 权限评估
     *
     * @param auth               authentication
     * @param returnObject       返回对象
     * @param permission         权限值 每个权限值必须用","隔开
     * @return boolean
     */
    @Override
    public boolean hasPermission(Authentication auth, Object returnObject, Object permission) {
        if ((auth == null) || (returnObject == null) || !(permission instanceof String)) {
            return false;
        }
        String[] var = ((String) permission).split(JwtTokenProvider.DELIMITER);
        return hasPrivilege(auth, var);
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable serializable, String returnObject, Object permission) {
        if ((auth == null) || (returnObject == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(auth, returnObject.toUpperCase(),
                            permission.toString().toUpperCase());
    }

    private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
        // need permission
        for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
            if (grantedAuth.getAuthority().startsWith(targetType)) {
                if (grantedAuth.getAuthority().contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }
}
