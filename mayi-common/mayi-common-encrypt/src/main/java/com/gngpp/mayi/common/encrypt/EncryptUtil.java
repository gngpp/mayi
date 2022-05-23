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

package com.gngpp.mayi.common.encrypt;

import com.gngpp.mayi.common.core.util.AESUtil;
import com.gngpp.mayi.common.core.util.StringUtil;
import com.gngpp.mayi.common.encrypt.property.AesProperties;
import com.gngpp.mayi.common.encrypt.property.RsaProperties;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * 包含自带rsa，aes加密封装
 *
 * @author mac
 * @date 2021/1/28
 **/
public class EncryptUtil {

    private static final String HMAC_SHA_1 = "HmacSHA1";
    private static final String HMAC_SHA_256 = "HmacSHA256";

    /**
     * rsa公钥加密
     *
     * @param content 需要加密内容
     * @return /
     * @throws Exception exception
     */
    public static String encryptForRsaByPublicKey(String content) throws Exception {
        return RsaUtil.encryptByPublicKey(RsaProperties.PUBLIC_KEY, content);
    }

    /**
     * rsa私钥加密
     *
     * @param content 需要加密内容
     * @return /
     * @throws Exception exception
     */
    public static String encryptForRsaByPrivateKey(String content) throws Exception {
        return RsaUtil.encryptByPrivateKey(RsaProperties.PRIVATE_KEY, content);
    }

    /**
     * rsa公钥解密
     *
     * @param content 加密内容
     * @return /
     * @throws Exception exception
     */
    public static String decryptForRsaByPublicKey(String content) throws Exception {
        return RsaUtil.decryptByPublicKey(RsaProperties.PUBLIC_KEY, content);
    }

    /**
     * rsa私钥解密
     *
     * @param content 加密内容
     * @return /
     * @throws Exception exception
     */
    public static String decryptForRsaByPrivateKey(String content) throws Exception {
        return RsaUtil.decryptByPrivateKey(RsaProperties.PRIVATE_KEY, content);
    }

    /**
     * aes加密 ecb模式
     *
     * @param content 需要加密内容
     * @return /
     */
    public static String encryptForAesByEcb(String content) {
        return AESUtil.encodeByECB(content, AesProperties.KEY);
    }

    /**
     * aes解密 ecb模式
     *
     * @param content 需要解密内容
     * @return /
     */
    public static String decryptForAesByEcb(String content) {
        return AESUtil.decodeByECB(content, AesProperties.KEY);
    }

    /**
     * aes加密 cbc模式
     *
     * @param content 需要加密内容
     * @return /
     */
    public static String encryptForAesByCbc(String content) {
        return AESUtil.encodeByCBC(content, AesProperties.KEY, AesProperties.IV);
    }

    public static String encryptForAesByCbc(byte[] content) {
        return encryptForAesByCbc(new String(content));
    }

    /**
     * aes解密 cbc模式
     *
     * @param content 需要解密内容
     * @return /
     */
    public static String decryptForAesByCbc(String content) {
        return AESUtil.decodeByCBC(content, AesProperties.KEY, AesProperties.IV);
    }

    public static String decryptForAesByCbc(byte[] contentByte) {
        return decryptForAesByCbc(new String(contentByte));
    }

    /**
     * 签名算法 hmac-sha-1
     *
     * @param content content
     * @return /
     */
    public static String signatureByHmacSha1(String content, String applyKey) {
        return toHmacSha(content, applyKey, HMAC_SHA_1);
    }

    /**
     * 签名算法 hmac-sha-256
     *
     * @param content content
     * @return /
     */
    public static String signatureByHmacSha256(String content, String applyKey) {
        return toHmacSha(content, applyKey, HMAC_SHA_256);
    }

    private static String toHmacSha(String content, String applyKey, String hmacSha256) {
        try {
            Mac mac = getMac(hmacSha256, applyKey);
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            byte[] encode = mac.doFinal(contentBytes);
            return byteToHex(encode);
        } catch (Exception e)  {
            e.printStackTrace();
        }
        return StringUtil.ENMPTY;
    }

    private static Mac getMac(String algorithm, String applyKey) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKey secretKey = new SecretKeySpec(applyKey.getBytes(StandardCharsets.UTF_8), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKey);
        return mac;
    }


    private static String byteToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

}
