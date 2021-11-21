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


import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author ant
 * Create by Ant on 2021/7/31 2:55 PM
 */
@SuppressWarnings("all")
public class RandomUtil {
    private static final String BASE_NUMBER = "0123456789";
    private static final String BASE_CHAR = "abcdefghijklmnopqrstuvwxyz";
    private static final String BASE_CHAR_NUMBER = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static String FORMAT = "0.00";
    private static Random random = new Random();

    public RandomUtil() {
    }

    public static int randomInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static int randomInt(int limit) {
        return random.nextInt(limit);
    }

    public static int randomInt() {
        return random.nextInt();
    }

    public static long randomLong() {
        return random.nextLong();
    }

    public static long randomLong(long min, long max) {
        long rangeLong = min + (long) ((new Random()).nextDouble() * (double) (max - min));
        return rangeLong;
    }

    public static String randomString(int length) {
        return randomString("abcdefghijklmnopqrstuvwxyz0123456789", length);
    }

    public static String randomNumbers(int length) {
        return randomString("0123456789", length);
    }

    public static String randomString(String baseString, int length) {
        StringBuffer sb = new StringBuffer();
        if (length < 1) {
            length = 1;
        }

        int baseLength = baseString.length();

        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(baseLength);
            sb.append(baseString.charAt(number));
        }

        return sb.toString();
    }

    public static double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public static double randomDouble() {
        return randomDouble(0.0D, 100.0D);
    }

    public static String randomDouble(String format) {
        return (new DecimalFormat(format)).format(randomDouble());
    }

    public static String randomValueByType(String type) {
        byte var2 = -1;
        switch (type.hashCode()) {
            case -1808118735:
                if (type.equals("String")) {
                    var2 = 0;
                }
                break;
            case -1325958191:
                if (type.equals("double")) {
                    var2 = 7;
                }
                break;
            case -672261858:
                if (type.equals("Integer")) {
                    var2 = 2;
                }
                break;
            case 104431:
                if (type.equals("int")) {
                    var2 = 3;
                }
                break;
            case 2122702:
                if (type.equals("Date")) {
                    var2 = 17;
                }
                break;
            case 2374300:
                if (type.equals("Long")) {
                    var2 = 4;
                }
                break;
            case 2606829:
                if (type.equals("Time")) {
                    var2 = 16;
                }
                break;
            case 3052374:
                if (type.equals("char")) {
                    var2 = 1;
                }
                break;
            case 3327612:
                if (type.equals("long")) {
                    var2 = 5;
                }
                break;
            case 64711720:
                if (type.equals("boolean")) {
                    var2 = 12;
                }
                break;
            case 67973692:
                if (type.equals("Float")) {
                    var2 = 8;
                }
                break;
            case 79860828:
                if (type.equals("Short")) {
                    var2 = 11;
                }
                break;
            case 97526364:
                if (type.equals("float")) {
                    var2 = 9;
                }
                break;
            case 109413500:
                if (type.equals("short")) {
                    var2 = 10;
                }
                break;
            case 798274969:
                if (type.equals("LocalDate")) {
                    var2 = 18;
                }
                break;
            case 1153828870:
                if (type.equals("LocalDateTime")) {
                    var2 = 20;
                }
                break;
            case 1438607953:
                if (type.equals("BigDecimal")) {
                    var2 = 14;
                }
                break;
            case 1729365000:
                if (type.equals("Boolean")) {
                    var2 = 13;
                }
                break;
            case 1854396478:
                if (type.equals("BigInteger")) {
                    var2 = 15;
                }
                break;
            case 2052876273:
                if (type.equals("Double")) {
                    var2 = 6;
                }
                break;
            case 2059094262:
                if (type.equals("Timestamp")) {
                    var2 = 19;
                }
        }

        switch (var2) {
            case 0:
                return randomString(6);
            case 1:
                return randomString(1);
            case 2:
                return String.valueOf(randomInt(1000));
            case 3:
                return String.valueOf(randomInt(1000));
            case 4:
                return String.valueOf(randomInt(1000));
            case 5:
                return String.valueOf(randomInt(1000));
            case 6:
                return String.valueOf(randomDouble(FORMAT));
            case 7:
                return String.valueOf(randomDouble(FORMAT));
            case 8:
                return String.valueOf(randomDouble(FORMAT));
            case 9:
                return String.valueOf(randomDouble(FORMAT));
            case 10:
                return String.valueOf(randomInt(0, 32767));
            case 11:
                return String.valueOf(randomInt(0, 32767));
            case 12:
                return "true";
            case 13:
                return "true";
            case 14:
                return String.valueOf(randomInt(1000));
            case 15:
                return String.valueOf(randomInt(1000));
            case 16:
                return DateTimeUtil.dateToStr(new Date(), "yyyy-MM-dd");
            case 17:
                return DateTimeUtil.dateToStr(new Date(), "yyyy-MM-dd");
            case 18:
                return DateTimeUtil.long2Str(System.currentTimeMillis(), "yyyy-MM-dd");
            case 19:
                return DateTimeUtil.long2Str(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
            case 20:
                return DateTimeUtil.long2Str(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
            default:
                return randomString(6);
        }
    }
}
