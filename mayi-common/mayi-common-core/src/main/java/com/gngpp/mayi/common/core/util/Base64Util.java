/*
 *
 *  * Copyright (c) 2021 gngpp
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

package com.gngpp.mayi.common.core.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author ant
 * Create by Ant on 2021/7/31 2:59 PM
 */
@SuppressWarnings("unused")
public class Base64Util {
    public Base64Util() {
    }

    public static byte[] decryptBASE64(byte[] encryptedData) {
        return Base64.getDecoder()
                     .decode(encryptedData);
    }

    public static byte[] decryptBASE64(String encryptedData) {
        return decryptBASE64(encryptedData.getBytes(StandardCharsets.UTF_8));
    }

    public static String decryptToString(String encryptedData) {
        return decryptToString(encryptedData.getBytes(StandardCharsets.UTF_8));
    }

    public static String decryptToString(byte[] encryptedData) {
        return new String(decryptBASE64(encryptedData));
    }

    public static byte[] encryptBASE64(byte[] decryptedData) {
        return Base64.getEncoder()
                     .encode(decryptedData);
    }

    public static byte[] encryptBASE64(String plaintext) {
        return encryptBASE64(plaintext.getBytes(StandardCharsets.UTF_8));
    }

    public static String encryptToString(String plaintext) {
        return encryptToString(plaintext.getBytes(StandardCharsets.UTF_8));
    }

    public static String encryptToString(byte[] plainByteArr) {
        return new String(encryptBASE64(plainByteArr));
    }
}
