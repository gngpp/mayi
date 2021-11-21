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

package com.zf1976.mayi.common.core.constants;

import java.nio.file.Paths;

/**
 * @author ant
 * Create by Ant on 2021/7/31 2:00 PM
 */
public interface MayiStandards {

    /**
     * Home
     */
    String HOME_PATH = System.getProperty("user.home");
    /**
     * 工作目录前缀
     */
    String WORK_NAME = ".mayi";
    /**
     * 临时目录前缀
     */
    String TEMP_NAME = ".temp";
    /**
     * 工作目录
     */
    String WORK_PATH = toAbsolutePath(HOME_PATH, WORK_NAME);
    /**
     * 临时文件目录
     */
    String TEMP_PATH = toAbsolutePath(WORK_PATH, TEMP_NAME);


    static String toAbsolutePath(String first, String... more) {
        return Paths.get(first, more)
                    .toFile()
                    .getAbsolutePath();
    }

}
