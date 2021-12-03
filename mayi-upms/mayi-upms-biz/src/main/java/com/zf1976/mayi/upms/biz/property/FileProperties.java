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

package com.zf1976.mayi.upms.biz.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 */
@Component
@ConfigurationProperties(prefix = "file.config")
public class FileProperties {

    private FileProperties.Relative relative;

    private FileProperties.Real real;

    /**
     * 文件大小
     */
    private String fileMaxSize;

    /**
     * 头像大小
     */
    private String avatarMaxSize;

    /**
     * 工作文件路径
     */
    private String workFilePath;

    /**
     * 头像完整路径
     */
    public static String avatarRealPath;

    /**
     * 文件完整路径
     */
    public static String fileRealPath;

    public Relative getRelative() {
        return relative;
    }

    public void setRelative(Relative relative) {
        this.relative = relative;
    }

    public Real getReal() {
        return real;
    }

    public void setReal(Real real) {
        this.real = real;
    }

    public String getFileMaxSize() {
        return fileMaxSize;
    }

    public void setFileMaxSize(String fileMaxSize) {
        this.fileMaxSize = fileMaxSize;
    }

    public String getAvatarMaxSize() {
        return avatarMaxSize;
    }

    public void setAvatarMaxSize(String avatarMaxSize) {
        this.avatarMaxSize = avatarMaxSize;
    }

    public String getWorkFilePath() {
        return workFilePath;
    }

    public void setWorkFilePath(String workFilePath) {
        this.workFilePath = workFilePath;
    }

    public static String getAvatarRealPath() {
        return avatarRealPath;
    }

    public static void setAvatarRealPath(String avatarRealPath) {
        FileProperties.avatarRealPath = avatarRealPath;
    }

    public static String getFileRealPath() {
        return fileRealPath;
    }

    public static void setFileRealPath(String fileRealPath) {
        FileProperties.fileRealPath = fileRealPath;
    }

    public static class Relative{

        /**
         * 头像url
         */
        private String avatarUrl;

        /**
         * 文件url
         */
        private String fileUrl;

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }

    public static class Real{

        /**
         * 头像相对路径
         */
        private String avatarPath;

        /**
         * 文件相对路径
         */
        private String filePath;

        public String getAvatarPath() {
            return avatarPath;
        }

        public void setAvatarPath(String avatarPath) {
            this.avatarPath = avatarPath;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
