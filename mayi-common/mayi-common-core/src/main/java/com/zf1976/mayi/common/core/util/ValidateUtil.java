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
 * Create by Ant on 2021/7/31 2:51 PM
 */
@SuppressWarnings("all")
public class ValidateUtil {
    public static final String EMAIL_PATTERN = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    public static final String CHINESE_PATTERN = "^[一-龥]{0,}$";
    public static final String NUMBER_ADN_LETTER = "^[A-Za-z0-9]+$";
    public static final String QQ_PATTERN = "/[1-9][0-9]{4,}/";
    public static final String NUMBER_PATTERN = "[0-9]+";
    public static final String LETTER_PATTERN = "[a-zA-Z]+";
    public static final String ZIPCODE_PATTERN = "\\p{Digit}{6}";
    public static final String PHONE_PATTERN = "^(13[0-9]|14[579]|15[^4,\\D]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
    public static final String TELEPHONE_PATTERN = "^(0\\d{2,3}-)?(\\d{7,8})(-(\\d{3,}))?$";
    public static final String TELEPHONE_400_PATTERN = "((400)(\\d{7}))|((400)-(\\d{3})-(\\d{4}))";
    public static final String IDCARD_PATTERN = "^((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65|71|81|82|91)\\d{4})((((19|20)(([02468][048])|([13579][26]))0229))|((20[0-9][0-9])|(19[0-9][0-9]))((((0[1-9])|(1[0-2]))((0[1-9])|(1\\d)|(2[0-8])))|((((0[1,3-9])|(1[0-2]))(29|30))|(((0[13578])|(1[02]))31))))((\\d{3}(x|X))|(\\d{4}))$";
    public static final String USERNAME_PATTERN = "^[A-Za-z0-9_]{3,15}$";
    public static final String PASSWORD_PATTERN = "^(?![0-9]+$)[0-9A-Za-z]{6,20}$";
    public static final String UUID_PATTERN = "[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}";
    public static final String YEAR_PATTERN = "^(19|20)\\d{2}$";
    public static final String TIME_PATTERN = "^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$";
    public static final String DATE_PATTERN = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
    public static final String TIME_STAMP_PATTERN = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\s]?((((0?[13578])|(1[02]))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\s]?((((0?[13578])|(1[02]))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\s]?((0?[1-9])|(1[0-9])|(2[0-8])))))) ([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])$";
    public static final String IPV4_PATTERN = "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";
    public static final String IPV6_PATTERN = "([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)";
    public static final String URL_PATTERN = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$#\\=~_\\-@]*)*$";
    public static final String DOMAIN_PATTERN = "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";
    public static final String INT_OR_FLOAT_PATTERN = "^\\d+\\.\\d+|\\d+$";
    public static final String FLOAT_PATTERN = "^(-?\\d+)(\\.\\d+)?$";
    public static final String POSITIVE_INTEGER = "[1-9]+\\d{0,10}";
    public static final String ARMY_ID_CARD = "南字第(\\d{6,8})号|北字第(\\d{6,8})号|沈字第(\\d{6,8})号|兰字第(\\d{6,8})号|成字第(\\d{6,8})号|济字第(\\d{6,8})号|广字第(\\d{6,8})号|海字第(\\d{6,8})号|空字第(\\d{6,8})号|参字第(\\d{6,8})号|政字第(\\d{6,8})号|后字第(\\d{6,8})号|装字第(\\d{6,8})号";
    public static final String[] forbidden = new String[]{"\\bselect\\b", "\\bor\\b", "\\bdelete\\b", "\\bjoin\\b", "\\btable\\b", "\\bdrop\\b", "\\biframe\\b", "\\bwindow\\b", "\\b_\\b", "\\+", "%", "\\<", "\\>", "'", "=", "%3C", "\\(", "%28", "alert", "eval((.*))", "script", "location.href"};

    public ValidateUtil() {
    }

    public static boolean validate(String str, String pattern) {
        return StringUtil.isNotEmpty(str) && str.matches(pattern);
    }

    public static boolean isEmail(String email) {
        return validate(email, "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }

    public static boolean isNotEmail(String email) {
        return !isEmail(email);
    }

    public static boolean isChinese(String chineseStr) {
        return validate(chineseStr, "^[一-龥]{0,}$");
    }

    public static boolean isNotChinese(String chineseStr) {
        return !validate(chineseStr, "^[一-龥]{0,}$");
    }

    public static boolean isNumberLetter(String str) {
        return validate(str, "^[A-Za-z0-9]+$");
    }

    public static boolean isNotNumberLetter(String str) {
        return !validate(str, "^[A-Za-z0-9]+$");
    }

    public static boolean isQq(String qq) {
        return validate(qq, "/[1-9][0-9]{4,}/");
    }

    public static boolean isNumber(String str) {
        return validate(str, "[0-9]+");
    }

    public static boolean isNotNumber(String str) {
        return !validate(str, "[0-9]+");
    }

    public static boolean isLetter(String str) {
        return validate(str, "[a-zA-Z]+");
    }

    public static boolean isZipCode(String zipCode) {
        return validate(zipCode, "\\p{Digit}{6}");
    }

    public static boolean isPhone(String phone) {
        return validate(phone, "^(13[0-9]|14[579]|15[^4,\\D]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$");
    }

    public static boolean isNotPhone(String phone) {
        return !isPhone(phone);
    }

    public static boolean isTelephone(String phoneNumber) {
        return validate(phoneNumber, "^(0\\d{2,3}-)?(\\d{7,8})(-(\\d{3,}))?$") || validate(phoneNumber, "((400)(\\d{7}))|((400)-(\\d{3})-(\\d{4}))");
    }

    public static boolean isNotTelephone(String phoneNumber) {
        return !isTelephone(phoneNumber);
    }

    public static boolean isIdCard(String cardNumber) {
        return validate(cardNumber, "^((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65|71|81|82|91)\\d{4})((((19|20)(([02468][048])|([13579][26]))0229))|((20[0-9][0-9])|(19[0-9][0-9]))((((0[1-9])|(1[0-2]))((0[1-9])|(1\\d)|(2[0-8])))|((((0[1,3-9])|(1[0-2]))(29|30))|(((0[13578])|(1[02]))31))))((\\d{3}(x|X))|(\\d{4}))$");
    }

    public static boolean isNotIdCard(String cardNumber) {
        return !validate(cardNumber, "^((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65|71|81|82|91)\\d{4})((((19|20)(([02468][048])|([13579][26]))0229))|((20[0-9][0-9])|(19[0-9][0-9]))((((0[1-9])|(1[0-2]))((0[1-9])|(1\\d)|(2[0-8])))|((((0[1,3-9])|(1[0-2]))(29|30))|(((0[13578])|(1[02]))31))))((\\d{3}(x|X))|(\\d{4}))$");
    }

    public static boolean isUserName(String str) {
        return validate(str, "^[A-Za-z0-9_]{3,15}$");
    }

    public static boolean isPassword(String str) {
        return validate(str, "^(?![0-9]+$)[0-9A-Za-z]{6,20}$");
    }

    public static boolean isNonnegativeInteger(String str) {
        return validate(str, "^\\d+$");
    }

    public static boolean isUuid(String str) {
        return validate(str, "[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}") || validate(str, "[0-9a-z]{32}");
    }

    public static boolean isNotUuid(String str) {
        return !isUuid(str);
    }

    public static boolean isDate(String date) {
        return validate(date, "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))");
    }

    public static boolean isNotDate(String date) {
        return !validate(date, "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))");
    }

    public static boolean isTimestamp(String date) {
        return validate(date, "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\s]?((((0?[13578])|(1[02]))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\s]?((((0?[13578])|(1[02]))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\s]?((0?[1-9])|(1[0-9])|(2[0-8])))))) ([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])$");
    }

    public static boolean isNotTimestamp(String date) {
        return !validate(date, "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\s]?((((0?[13578])|(1[02]))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\s]?((((0?[13578])|(1[02]))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\s]?((0?[1-9])|(1[0-9])|(2[0-8])))))) ([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])$");
    }

    public static boolean isIP(String ip) {
        if (StringUtil.isEmpty(ip)) {
            return false;
        } else {
            ip = ip.toLowerCase();
            return validate(ip, "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])") || validate(ip, "([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)\\:([0-9a-f]+)");
        }
    }

    public static boolean isNotIP(String ip) {
        return !isIP(ip);
    }

    public static boolean isUrl(String url) {
        return validate(url, "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$#\\=~_\\-@]*)*$");
    }

    public static boolean isNotUrl(String url) {
        return !validate(url, "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$#\\=~_\\-@]*)*$");
    }

    public static boolean isDomain(String domain) {
        return validate(domain, "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$");
    }

    public static boolean isNotDomain(String domain) {
        return !validate(domain, "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$");
    }

    public static boolean isIntOrFloat(String number) {
        return validate(number, "^\\d+\\.\\d+|\\d+$");
    }

    public static boolean isNotIntOrFloat(String number) {
        return !validate(number, "^\\d+\\.\\d+|\\d+$");
    }

    public static boolean isFloat(String number) {
        return validate(number, "^(-?\\d+)(\\.\\d+)?$");
    }

    public static boolean isNotFloat(String number) {
        return !validate(number, "^(-?\\d+)(\\.\\d+)?$");
    }

    public static boolean isNegativeFloat(String number) {
        return validate(number, "^(-((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*)))$");
    }

    public static boolean isNotNegativeFloat(String number) {
        return !isNegativeFloat(number);
    }

    public static boolean isPositiveFloat(String number) {
        return validate(number, "^((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*))$");
    }

    public static boolean isNotPositiveFloat(String number) {
        return !isPositiveFloat(number);
    }

    public static boolean isPositiveInteger(String number) {
        return validate(number, "[1-9]+\\d{0,10}");
    }

    public static boolean isArmyIdCard(String armyIdCard) {
        return validate(armyIdCard, "南字第(\\d{6,8})号|北字第(\\d{6,8})号|沈字第(\\d{6,8})号|兰字第(\\d{6,8})号|成字第(\\d{6,8})号|济字第(\\d{6,8})号|广字第(\\d{6,8})号|海字第(\\d{6,8})号|空字第(\\d{6,8})号|参字第(\\d{6,8})号|政字第(\\d{6,8})号|后字第(\\d{6,8})号|装字第(\\d{6,8})号");
    }

    public static boolean isNotArmyIdCard(String armyIdCard) {
        return !validate(armyIdCard, "南字第(\\d{6,8})号|北字第(\\d{6,8})号|沈字第(\\d{6,8})号|兰字第(\\d{6,8})号|成字第(\\d{6,8})号|济字第(\\d{6,8})号|广字第(\\d{6,8})号|海字第(\\d{6,8})号|空字第(\\d{6,8})号|参字第(\\d{6,8})号|政字第(\\d{6,8})号|后字第(\\d{6,8})号|装字第(\\d{6,8})号");
    }

    public static boolean isContainsForbiddenCharacter(String number) {
        if (StringUtil.isEmpty(number)) {
            return false;
        } else {
            String[] var1 = forbidden;
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                String reg = var1[var3];
                number = number.toLowerCase();
                String me = number.replaceAll(reg, "invalid character");
                if (me.length() != number.length()) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isYear(String yearNumber) {
        return validate(yearNumber, "^(19|20)\\d{2}$");
    }

    public static boolean isNotYear(String yearNumber) {
        return !isYear(yearNumber);
    }

    public static boolean isTime(String time) {
        return validate(time, "^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$");
    }

    public static boolean isNotTime(String time) {
        return !isTime(time);
    }
}
