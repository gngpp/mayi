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

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author ant
 * Create by Ant on 2021/7/31 2:46 PM
 */
@SuppressWarnings("all")
public class DateTimeUtil {
    public static final String DATE_FORMAT_MINITE = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
    public static final String DATE_FORMAT_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_SECOND_12 = "yyyy-MM-dd hh:mm:ss";
    public static final String DATE_FORMAT_MILLISECOND = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT_CHINESE = "yyyy年MM月dd日";
    public static final String DATE_FORMAT_CHINESE_SECONDE = "yyyy年MM月dd日 HH:mm:ss";
    public static final String DATE_FORMAT_CHINESE_WEEK_SECONDE = "yyyy年MM月dd日 E HH:mm:ss";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
    public static final long DAY_MS = 86400000L;
    private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();
    private static final int PATTERN_CACHE_SIZE = 500;
    private static final String[] WEEK_ARR = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public DateTimeUtil() {
    }

    public static String dateToStr(Date date, String format) {
        return dateToStr(date, format, (Locale) null);
    }

    public static String nowStrTime() {
        return long2Str(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String nowStrTime(String pattern) {
        return long2Str(System.currentTimeMillis(), pattern);
    }

    public static String dateToStr(Date date, String pattern, Locale locale) {
        return format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()), pattern);
    }

    public static String sqlDateToStr(java.sql.Date date, String format) {
        return format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()), format);
    }

    public static Date strToDate(String sDate, String format) {
        LocalDate localDate = parseLocalDate(sDate, format);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static Timestamp nowTimeStamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp strToStamp(String date) {
        return strToStamp(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Timestamp strToStamp(String date, String format) {
        LocalDateTime localDateTime = parseLocalDateTime(date, format);
        return Timestamp.valueOf(localDateTime);
    }

    public static double calcRunTime(long beginTime, long endTime) {
        return (double) (endTime - beginTime);
    }

    public static void printRunTime(long beganTime, long endTime) {
        System.out.println("CostTime->" + (double) (endTime - beganTime) / 1000.0D + " Seconds");
    }

    public static Long getNowTime() {
        return System.currentTimeMillis();
    }

    public static long getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (afterTime - beforeTime) / 86400000L;
    }

    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    public static String timestampToString(Timestamp time, String fmt) {
        DateTimeFormatter dateTimeFormatter = createCacheFormatter(fmt);
        LocalDateTime dateTime = time.toLocalDateTime();
        return dateTimeFormatter.format(dateTime);
    }

    public static long setTimeToNextDay0H0M0S(Timestamp time) {
        if (time != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time.getTime());
            cal.add(5, 1);
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            return cal.getTimeInMillis();
        } else {
            throw new NullPointerException("Timestamp can not be null");
        }
    }

    public static long setTimeToNextDay0H0M0S(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.add(5, 1);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTimeInMillis();
    }

    public static long setToNextDay0H0M0SExceptToday(long millis) {
        boolean flag = isDifferentDay(System.currentTimeMillis(), millis);
        long finalTime;
        if (flag) {
            finalTime = setTimeToNextDay0H0M0S(millis);
        } else {
            finalTime = System.currentTimeMillis();
        }

        return finalTime;
    }

    public static long setToNextDay0H0M0SExceptToday(Timestamp stamp) {
        if (stamp != null) {
            boolean flag = isDifferentDay(nowTimeStamp(), stamp);
            long finalTime;
            if (flag) {
                finalTime = setTimeToNextDay0H0M0S(stamp);
            } else {
                finalTime = System.currentTimeMillis();
            }

            return finalTime;
        } else {
            throw new NullPointerException("Timestamp can not be null");
        }
    }

    public static long setTimeTo0H0M0S(Timestamp time) {
        if (time != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time.getTime());
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            return cal.getTimeInMillis();
        } else {
            throw new NullPointerException("Timestamp can not be null");
        }
    }

    public static long setTimeTo0H0M0S(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTimeInMillis();
    }

    public static void setTimeToLastDay0H0M0S(Timestamp time) {
        if (time != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time.getTime());
            cal.add(5, -1);
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            time.setTime(cal.getTimeInMillis());
        } else {
            throw new NullPointerException("Timestamp can not be null");
        }
    }

    public static boolean isDifferentDay(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(1) != calendar2.get(1) || calendar1.get(6) != calendar2.get(6);
    }

    public static boolean isDifferentDay(Timestamp timestamp0, Timestamp timestamp1) {
        if (timestamp0 != null && timestamp1 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(timestamp0.getTime());
            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(timestamp1.getTime());
            return isDifferentDay(cal1, cal2);
        } else {
            throw new NullPointerException("Timestamp can not be null");
        }
    }

    public static boolean isDifferentDay(long millis0, long millis1) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(millis0);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(millis1);
        return isDifferentDay(cal1, cal2);
    }

    public static String long2Str(Long millSec) {
        return long2Str(millSec, "yyyy年MM月dd日");
    }

    public static String long2Str(long millSec, String format) {
        return long2Str(millSec, format, Locale.CHINESE);
    }

    public static String long2Str(long millSec, String format, Locale locale) {
        Date date = new Date(millSec);
        return dateToStr(date, format);
    }

    public static long strToLong(String strTime, String pattern) {
        try {
            return LocalDateTimeToLong(parseLocalDateTime(strTime, pattern));
        } catch (Exception var3) {
            return localDateToLong(parseLocalDate(strTime, pattern));
        }
    }

    private static boolean isEmpty(String str) {
        return null == str || "".equals(str.trim()) || "null".equals(str.trim()) || "NaN".equals(str.trim());
    }

    public static long todayPastMillisecond(long millions) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millions);
        return (long) (cal.get(11) * 3600 + cal.get(12) * 60);
    }

    public static int getCurrentMonthDays() {
        Calendar a = Calendar.getInstance();
        a.set(5, 1);
        a.roll(5, -1);
        return a.get(5);
    }

    public static int getCurrentMonthDays(Timestamp stamp) {
        if (null != stamp) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(stamp.getTime());
            return getCurrentMonthDays(cal);
        } else {
            throw new NullPointerException("Timestamp can not be null");
        }
    }

    public static int getCurrentMonthDays(long ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        return getCurrentMonthDays(cal);
    }

    public static int getCurrentMonthDays(Calendar cal) {
        if (null != cal) {
            cal.set(5, 1);
            cal.roll(5, -1);
            return cal.get(5);
        } else {
            throw new NullPointerException("Calendar can not be null");
        }
    }

    public static long getFirstDayOfCurrentWeek(Timestamp stamp) {
        if (null != stamp) {
            return getFirstDayOfCurrentWeek(stamp.getTime());
        } else {
            throw new NullPointerException("Timestamp can not be null");
        }
    }

    public static long getFirstDayOfCurrentWeek(long ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        int dayOfWeek = cal.get(7);
        if (dayOfWeek == 1) {
            cal.set(7, 2);
            cal.add(3, -1);
        } else {
            cal.set(7, 2);
        }

        return cal.getTimeInMillis();
    }

    public static long setToFirstDayOfCurrentYear(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(6, 1);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTimeInMillis();
    }

    public static long setToFirstDayOfNextYear(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(6, 1);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        cal.add(1, 1);
        return cal.getTimeInMillis();
    }

    public static long setToFirstDayOfLastMonth(long ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        cal.add(2, -1);
        cal.set(5, 1);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTimeInMillis();
    }

    public static long setToLastMonthCommonDay(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        calendar.add(2, -1);
        return calendar.getTimeInMillis();
    }

    public static long setToFirstDayOfCurrentMonth(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(5, 1);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTimeInMillis();
    }

    public static long setToFirstDayOfNextMonth(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.add(2, 1);
        cal.set(5, 1);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTimeInMillis();
    }

    public static long setToNextYearCommonDay(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(1, cal.get(1) + 1);
        return cal.getTimeInMillis();
    }

    public static long setToLastYearCommonDay(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        calendar.set(1, calendar.get(1) - 1);
        return calendar.getTimeInMillis();
    }

    public static long getLastDayOfCurrentWeek(Timestamp stamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(stamp.getTime());
        cal.set(7, 1);
        cal.add(3, 1);
        return cal.getTimeInMillis();
    }

    public static long getFirstDayOfCurrentQuarter(long ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        int currentMonth = cal.get(2) + 1;
        if (currentMonth >= 1 && currentMonth <= 3) {
            cal.set(2, 0);
        } else if (currentMonth >= 4 && currentMonth <= 6) {
            cal.set(2, 3);
        } else if (currentMonth >= 7 && currentMonth <= 9) {
            cal.set(2, 6);
        } else if (currentMonth >= 10 && currentMonth <= 12) {
            cal.set(2, 9);
        }

        cal.set(5, 1);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTimeInMillis();
    }

    public static long getFirstDayOfNextQuarter(long ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        int currentMonth = cal.get(2) + 1;
        if (currentMonth >= 1 && currentMonth <= 3) {
            cal.set(2, 2);
            cal.set(5, 31);
        } else if (currentMonth >= 4 && currentMonth <= 6) {
            cal.set(2, 5);
            cal.set(5, 30);
        } else if (currentMonth >= 7 && currentMonth <= 9) {
            cal.set(2, 8);
            cal.set(5, 30);
        } else if (currentMonth >= 10 && currentMonth <= 12) {
            cal.set(2, 11);
            cal.set(5, 31);
        }

        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTimeInMillis() + 86400000L;
    }

    public static int getDayOfWeek(long ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        int a = cal.get(7);
        return a >= 2 ? a - 1 : 7;
    }

    public static long create0H0M0STime() {
        return setTimeTo0H0M0S(System.currentTimeMillis());
    }

    public static boolean isToday(long ms) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(ms);
        return !isDifferentDay(cal1, cal2);
    }

    public static String friendlyTime(long ms) {
        String ftime = "";
        Calendar cal = Calendar.getInstance();
        if (isDifferentDay(System.currentTimeMillis(), ms)) {
            int hour = (int) ((cal.getTimeInMillis() - ms) / 3600000L);
            if (hour == 0) {
                ftime = Math.max((cal.getTimeInMillis() - ms) / 60000L, 1L) + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
        }

        long lt = ms / 86400000L;
        long ct = cal.getTimeInMillis() / 86400000L;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - ms) / 3600000L);
            if (hour == 0) {
                ftime = Math.max((cal.getTimeInMillis() - ms) / 60000L, 1L) + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = long2Str(ms);
        }

        return ftime;
    }

    public static int getWeeks(long startTime, long endTime) {
        int temp = 0;

        try {
            temp = (int) ((endTime - startTime) / 86400000L / 7L);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return temp;
    }

    public static String getDateWithWeek(long ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        int a = cal.get(7);
        String[] weekARR = new String[]{"（周日）", "（周一）", "（周二）", "（周三）", "（周四）", "（周五）", "（周六）"};
        return long2Str(ms, "MM月dd日" + weekARR[a - 1]);
    }

    public static String getDateWithWeekAndTime(long ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        int a = cal.get(7);
        return long2Str(ms, "yyyy年MM月dd日 " + WEEK_ARR[a - 1] + " HH:mm");
    }

    public static int getAge(long ms) {
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        born.setTimeInMillis(ms);
        if (born.after(now)) {
            return -1;
        } else {
            int age = now.get(1) - born.get(1);
            if (now.get(6) < born.get(6)) {
                --age;
            }

            return age;
        }
    }

    public static String getLastYearCommonDay(String strDate, String format) {
        Date date = strToDate(strDate, format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(1, calendar.get(1) - 1);
        return long2Str(calendar.getTimeInMillis(), format);
    }

    public static String getLastMonthCommonDay(String strDate, String format) {
        Date date = strToDate(strDate, format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int a = calendar.get(5);
        int b = getCurrentMonthDays(date.getTime());
        if (a == b) {
            long ms = setToFirstDayOfCurrentMonth(date.getTime()) - 86400000L;
            return long2Str(ms, format);
        } else {
            calendar.add(2, -1);
            return long2Str(calendar.getTimeInMillis(), format);
        }
    }

    public static Timestamp getBirthdayFormIdCard(String idCard) {
        if (ValidateUtil.isIdCard(idCard)) {
            Timestamp timestamp;
            if (idCard.length() == 18) {
                timestamp = strToStamp(idCard.substring(6, 14), "yyyyMMdd");
            } else {
                timestamp = strToStamp(idCard.substring(6, 12), "yyyyMMdd");
            }

            return timestamp;
        } else {
            throw new RuntimeException("invalid IdChard number:" + idCard);
        }
    }

    public static long getLongBirthFormIdCard(String idCard) {
        return getBirthdayFormIdCard(idCard).getTime();
    }

    public static boolean isCurrentYear(long ms) {
        Calendar calTemp = Calendar.getInstance();
        calTemp.setTimeInMillis(ms);
        int yearTemp = calTemp.get(1);
        Calendar calNow = Calendar.getInstance();
        int yearNow = calNow.get(1);
        return yearNow == yearTemp;
    }

    public static String format(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return localDateTime.format(formatter);
    }

    private static DateTimeFormatter createCacheFormatter(String pattern) {
        if (pattern != null && pattern.length() != 0) {
            DateTimeFormatter formatter = (DateTimeFormatter) FORMATTER_CACHE.get(pattern);
            if (formatter == null && FORMATTER_CACHE.size() < 500) {
                formatter = DateTimeFormatter.ofPattern(pattern);
                DateTimeFormatter oldFormatter = (DateTimeFormatter) FORMATTER_CACHE.putIfAbsent(pattern, formatter);
                if (oldFormatter != null) {
                    formatter = oldFormatter;
                }
            }

            return formatter;
        } else {
            throw new IllegalArgumentException("Invalid pattern specification");
        }
    }

    public static long localDateToLong(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneId.systemDefault())
                        .toEpochSecond() * 1000L;
    }

    public static LocalDate parseLocalDate(String time, String pattern) {
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return LocalDate.parse(time, formatter);
    }

    public static LocalDateTime parseLocalDateTime(String time, String pattern) {
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return LocalDateTime.parse(time, formatter);
    }

    public static long LocalDateTimeToLong(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.of("+8"))
                       .toEpochMilli();
    }
}
