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

package com.zf1976.mayi.common.core.util;

/**
 * @author mac
 * 2021/12/5 星期日 2:07 PM
 */
public class BooleanUtil {

    public BooleanUtil() {
    }

    public static Boolean negate(Boolean bool) {
        if (bool == null) {
            return null;
        } else {
            return bool ? Boolean.FALSE : Boolean.TRUE;
        }
    }

    public static boolean isTrue(Boolean bool) {
        return Boolean.TRUE.equals(bool);
    }

    public static boolean isFalse(Boolean bool) {
        return Boolean.FALSE.equals(bool);
    }

    public static boolean negate(boolean bool) {
        return !bool;
    }

    public static int toInt(boolean value) {
        return value ? 1 : 0;
    }

    public static Integer toInteger(boolean value) {
        return toInt(value);
    }

    public static char toChar(boolean value) {
        return (char)toInt(value);
    }

    public static Character toCharacter(boolean value) {
        return toChar(value);
    }

    public static byte toByte(boolean value) {
        return (byte)toInt(value);
    }

    public static Byte toByteObj(boolean value) {
        return toByte(value);
    }

    public static long toLong(boolean value) {
        return (long)toInt(value);
    }

    public static Long toLongObj(boolean value) {
        return toLong(value);
    }

    public static short toShort(boolean value) {
        return (short)toInt(value);
    }

    public static Short toShortObj(boolean value) {
        return toShort(value);
    }

    public static float toFloat(boolean value) {
        return (float)toInt(value);
    }

    public static Float toFloatObj(boolean value) {
        return toFloat(value);
    }

    public static double toDouble(boolean value) {
        return (double)toInt(value);
    }

    public static Double toDoubleObj(boolean value) {
        return toDouble(value);
    }

    public static String toStringTrueFalse(boolean bool) {
        return toString(bool, "true", "false");
    }

    public static String toStringOnOff(boolean bool) {
        return toString(bool, "on", "off");
    }

    public static String toStringYesNo(boolean bool) {
        return toString(bool, "yes", "no");
    }

    public static String toString(boolean bool, String trueString, String falseString) {
        return bool ? trueString : falseString;
    }
    public static boolean isBoolean(Class<?> clazz) {
        return clazz == Boolean.class || clazz == Boolean.TYPE;
    }
}
