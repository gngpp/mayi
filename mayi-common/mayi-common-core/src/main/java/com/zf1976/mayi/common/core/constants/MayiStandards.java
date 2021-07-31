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
