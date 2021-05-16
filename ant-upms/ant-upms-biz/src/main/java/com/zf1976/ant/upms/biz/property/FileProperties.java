package com.zf1976.ant.upms.biz.property;

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
