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

