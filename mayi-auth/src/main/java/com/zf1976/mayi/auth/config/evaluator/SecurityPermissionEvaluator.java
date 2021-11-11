package com.zf1976.mayi.auth.config.evaluator;

import com.zf1976.mayi.auth.deprecate.JwtTokenProvider;
import com.zf1976.mayi.common.security.support.session.manager.SessionManagement;
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
        if (SessionManagement.isOwner()) {
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
