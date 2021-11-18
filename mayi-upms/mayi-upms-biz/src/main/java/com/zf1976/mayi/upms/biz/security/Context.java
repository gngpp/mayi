package com.zf1976.mayi.upms.biz.security;

import com.zf1976.mayi.common.security.property.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class Context extends SecurityContextHolder {

    private static SecurityProperties securityProperties;

    @Autowired
    public void setSecurityProperties(SecurityProperties securityProperties) {
        Context.securityProperties = securityProperties;
    }

   public static boolean isOwner() {
       String name = getContext().getAuthentication().getName();
       return ObjectUtils.nullSafeEquals(name, securityProperties.getOwner());
   }

   public static String getUsername() {
       return getContext().getAuthentication().getName();
   }

}
