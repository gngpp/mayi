/*
 *
 *  * Copyright (c) 2021 zf1976
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

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
