package com.zf1976.mayi.common.security.access;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mac
 * 2021/12/3 星期五 9:50 PM
 */
public class PermissionAttribute implements ConfigAttribute {

    private String attribute;

    public PermissionAttribute() {
    }

    public PermissionAttribute(String permission) {
        Assert.hasText(permission, "You must provide a configuration attribute");
        this.attribute = permission;
    }

    @Override
    public String getAttribute() {
        return this.attribute;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConfigAttribute) {
            ConfigAttribute attr = (ConfigAttribute) obj;
            return this.attribute.equals(attr.getAttribute());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.attribute.hashCode();
    }

    @Override
    public String toString() {
        return this.attribute;
    }


    public static List<ConfigAttribute> createListFromCommaDelimitedString(String access) {
        return createList(StringUtils.commaDelimitedListToStringArray(access));
    }

    public static List<ConfigAttribute> createList(String... attributeNames) {
        Assert.notNull(attributeNames, "You must supply an array of attribute names");
        List<ConfigAttribute> attributes = new ArrayList<>(attributeNames.length);
        for (String attribute : attributeNames) {
            attributes.add(new SecurityConfig(attribute.trim()));
        }
        return attributes;
    }
}
