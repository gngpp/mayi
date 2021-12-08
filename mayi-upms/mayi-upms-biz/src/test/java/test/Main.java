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

package test;

import com.zf1976.mayi.common.core.util.Base64Util;
import com.zf1976.mayi.common.core.util.HexUtil;
import com.zf1976.mayi.common.core.util.UUIDUtil;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

/**
 * @author ant
 * Create by Ant on 2021/7/31 2:19 PM
 */
public class Main {
    public static void main(String[] args) {
       String str = "Query{page=1, size=5, sort=null, orders=null, query=null}";
       String str2 = "demp";
        System.out.println(Base64Util.encryptToString(str2));
        System.out.println(Base64Util.encryptToString(str));
        System.out.println(HexUtil.byteArr2HexStr(str.getBytes(StandardCharsets.UTF_8)));
        System.out.println(HexUtil.byteArr2HexStr(str2.getBytes(StandardCharsets.UTF_8)));
    }
}
