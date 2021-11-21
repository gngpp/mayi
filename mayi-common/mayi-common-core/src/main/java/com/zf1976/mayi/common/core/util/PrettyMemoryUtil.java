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

package com.zf1976.mayi.common.core.util;

/**
 * @author ant
 * Create by Ant on 2021/7/31 3:48 PM
 */
@SuppressWarnings("all")
public class PrettyMemoryUtil {
    private static final int UNIT = 1024;

    public PrettyMemoryUtil() {
    }

    public static String prettyByteSize(long byteSize) {
        double size = (double) byteSize;
        String type = "B";
        if ((int) Math.floor(size / 1024.0D) <= 0) {
            type = "B";
            return format(size, type);
        } else {
            size /= 1024.0D;
            if ((int) Math.floor(size / 1024.0D) <= 0) {
                type = "KB";
                return format(size, type);
            } else {
                size /= 1024.0D;
                if ((int) Math.floor(size / 1024.0D) <= 0) {
                    type = "MB";
                    return format(size, type);
                } else {
                    size /= 1024.0D;
                    if ((int) Math.floor(size / 1024.0D) <= 0) {
                        type = "GB";
                        return format(size, type);
                    } else {
                        size /= 1024.0D;
                        if ((int) Math.floor(size / 1024.0D) <= 0) {
                            type = "TB";
                            return format(size, type);
                        } else {
                            size /= 1024.0D;
                            if ((int) Math.floor(size / 1024.0D) <= 0) {
                                type = "PB";
                                return format(size, type);
                            } else {
                                return ">PB";
                            }
                        }
                    }
                }
            }
        }
    }

    private static String format(double size, String type) {
        byte precision;
        if (size * 1000.0D % 10.0D > 0.0D) {
            precision = 3;
        } else if (size * 100.0D % 10.0D > 0.0D) {
            precision = 2;
        } else if (size * 10.0D % 10.0D > 0.0D) {
            precision = 1;
        } else {
            precision = 0;
        }

        String formatStr = "%." + precision + "f";
        if ("KB".equals(type)) {
            return String.format(formatStr, size) + "KB";
        } else if ("MB".equals(type)) {
            return String.format(formatStr, size) + "MB";
        } else if ("GB".equals(type)) {
            return String.format(formatStr, size) + "GB";
        } else if ("TB".equals(type)) {
            return String.format(formatStr, size) + "TB";
        } else {
            return "PB".equals(type) ? String.format(formatStr, size) + "PB" : String.format(formatStr, size) + "B";
        }
    }
}

