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

package com.zf1976.mayi.upms.biz.service.util;

import org.apache.ibatis.reflection.ReflectionException;

import java.util.Locale;

/**
 * @author mac
 * @date 2020/12/24
 **/
public final class PropertyNameUtils {

    public static final String IS = "is";
    public static final String GET = "get";
    public static final String SET = "set";

    private PropertyNameUtils() {
    }

    public static String methodToProperty(String name) {
        if (name.startsWith(IS)) {
            name = name.substring(2);
        } else {
            if (!name.startsWith(GET) && !name.startsWith(SET)) {
                throw new ReflectionException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            }

            name = name.substring(3);
        }

        if (name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

    public static boolean isProperty(String name) {
        return isGetter(name) || isSetter(name);
    }

    public static boolean isGetter(String name) {
        return name.startsWith(GET) && name.length() > 3 || name.startsWith(IS) && name.length() > 2;
    }

    public static boolean isSetter(String name) {
        return name.startsWith(SET) && name.length() > 3;
    }
}
