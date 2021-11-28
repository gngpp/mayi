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
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.upms.biz.security.backup.service;

import com.zf1976.mayi.common.core.util.PrettyMemoryUtil;
import com.zf1976.mayi.upms.biz.security.backup.MySQLBackupStrategy;
import com.zf1976.mayi.upms.biz.security.backup.SQLBackupStrategy;
import com.zf1976.mayi.upms.biz.security.backup.exception.SQLBackupException;
import com.zf1976.mayi.upms.biz.security.backup.property.SQLBackupProperties;
import com.zf1976.mayi.upms.biz.security.pojo.BackupFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ant
 * Create by Ant on 2021/6/8 4:53 上午
 */
@Service(value = "mySQLBackupService")
public class MySQLBackupService {

    private final Logger log = LoggerFactory.getLogger("MySQLBackupService");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final SQLBackupStrategy sqlBackupStrategy;
    private final SQLBackupProperties properties;
    private final int pageCount;

    public MySQLBackupService(DataSource dataSource, SQLBackupProperties properties) {
        this.sqlBackupStrategy = new MySQLBackupStrategy(dataSource);
        this.properties = properties;
        this.pageCount = properties.getDayTotal() / properties.getFileCountSize();
    }

    /**
     * 创建备份
     *
     * @return {@link Void}
     */
    public Void createBackup() {
        // 备份文件目录
        var backupFileDirectory = this.getBackupParentFileDirectory();
        // 根据目录存在备份子目录过滤
        var dateDirectoryArray = this.getArrayFilterHiddenAndDirectory(backupFileDirectory);

        // 按日期划分目录不存在
        if (dateDirectoryArray == null) {
            this.createBackupFileByDefault();
            return null;
        }

        // 按备份日期划分
        var dateFileDirectory = this.getBackupDateFileDirectory();
        // 过滤子目录文件列表
        var childFileDirectoryArray = this.getChildFileAndFilter(dateFileDirectory);
        // 存在按0-9序号划分目录
        boolean createNewIndexDirectory = false;
        if (childFileDirectoryArray == null) {
           this.createBackupFileByDefault();
        } else {
            // 按0-9序号划分
            for (File childFileDirectory : childFileDirectoryArray) {
                // 重置标记
                createNewIndexDirectory = false;
                File[] backupFileArray = childFileDirectory.listFiles();
                // 当前目录存在备份文件
                if (backupFileArray != null) {
                    List<File> backupFileList = Arrays.stream(backupFileArray)
                                                      .filter(file -> !file.isHidden() && file.getName()
                                                                                              .startsWith(this.sqlBackupStrategy.getDatabase()))
                                                      .collect(Collectors.toList());
                    // 当目录备份文件数小于限定
                    if (backupFileList.size() < this.properties.getFileCountSize()) {
                        // 创建备份文件成功退出
                        if (this.sqlBackupStrategy.backup(childFileDirectory)) {
                            break;
                        }
                    } else {
                        // 新增目录上限判断
                        if ((childFileDirectoryArray.length + 1) <= this.pageCount) {
                            createNewIndexDirectory = true;
                        } else {
                            throw new SQLBackupException("Maximum number of backup files created that day");
                        }
                    }
                }
            }
            // 创建新目录并备份
            if (createNewIndexDirectory && (childFileDirectoryArray.length + 1) <= this.pageCount) {
                File backupChildFileDirectory = this.getBackupChildFileDirectory(childFileDirectoryArray.length);
                this.createBackupFile(backupChildFileDirectory);
            }
        }
        return null;
    }

    /**
     * 按字符串日期进行分页查询备份文件
     *
     * @param date 自定义字符串日期格式
     * @return {@link List<BackupFile>}
     */
    public List<BackupFile> selectBackupFileByDate(String date, Integer page){
        if (StringUtils.isEmpty(date)) {
            return Collections.emptyList();
        }
        File backupParentFileDirectory = this.getBackupParentFileDirectory();
        File[] dateFileDirectoryArray = this.getArrayFilterHiddenAndDirectory(backupParentFileDirectory);
        if (dateFileDirectoryArray != null) {
            File targetFileDirectory = null;
            for (File dateFileDirectory : dateFileDirectoryArray) {
                if (dateFileDirectory.getName().equals(date)) {
                    targetFileDirectory = dateFileDirectory;
                    break;
                }
            }
            if (targetFileDirectory != null) {
                if (page != null && page > 0 && page <= this.pageCount) {
                    File[] childFileDirectory = this.getArrayFilterHiddenAndDirectory(targetFileDirectory);
                    if (childFileDirectory != null) {
                        File[] targetFileArray = childFileDirectory[page].listFiles(pathname -> !pathname.isHidden() && pathname.getName()
                                                                                                                      .startsWith(this.sqlBackupStrategy.getDatabase()));
                        if (targetFileArray != null) {
                            List<BackupFile> targetFileList = new LinkedList<>();
                            for (File file : targetFileArray) {
                                BackupFile backupFile = new BackupFile();
                                backupFile.setName(file.getName())
                                          .setSize(PrettyMemoryUtil.prettyByteSize(file.length()))
                                          .setDirectory(file.isDirectory())
                                          .setCanRead(file.canRead())
                                          .setCanWrite(file.canWrite())
                                          .setHidden(file.isHidden())
                                          .setLastModifyDate(new Date(file.lastModified()))
                                          .setMd5(this.getFileMD5(file));
                                targetFileList.add(backupFile);
                            }
                            return targetFileList;
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    public List<String> selectBackupDate() {
        File backupParentFileDirectory = this.getBackupParentFileDirectory();
        File[] files = backupParentFileDirectory.listFiles();
        if (files != null) {
            return Arrays.stream(files)
                         .filter(file -> !file.isHidden())
                         .map(File::getName)
                         .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 根据文件名进行删除备份文件(类似递归查找)
     *
     * @param filename 文件名
     * @return {@link Void}
     */
    public Void deleteBackupFileByFilename(String filename) {
        File backupParentFileDirectory = this.getBackupParentFileDirectory();
        File[] dateFileDirectoryArray = this.getArrayFilterHiddenAndDirectory(backupParentFileDirectory);
        if (dateFileDirectoryArray != null) {
            for (File dateFileDirectory : dateFileDirectoryArray) {
                File[] childFileDirectoryArray = this.getArrayFilterHiddenAndDirectory(dateFileDirectory);
                for (File childFileDirectory : childFileDirectoryArray) {
                    File[] backupFileArray = childFileDirectory.listFiles(pathname -> !pathname.isHidden() && pathname.getName().startsWith(this.sqlBackupStrategy.getDatabase()));
                    if (backupFileArray != null) {
                        for (File backupFile : backupFileArray) {
                            if (backupFile.getName().equals(filename)) {
                                try {
                                    Files.delete(backupFile.toPath());
                                    return null;
                                } catch (IOException e) {
                                    log.error(e.getMessage(), e.getCause());
                                    throw new SQLBackupException("Failed to delete file：" + filename, e.getCause());
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private File[] getChildFileAndFilter(File dateFileDirectory) {
        return dateFileDirectory.listFiles(pathname -> {
            // Filter directory file names by number
            for (int i = 0; i < this.pageCount; i++) {
                if (String.valueOf(i).equals(pathname.getName())) {
                    return true;
                }
            }
            return false;
        });
    }

    private File[] getArrayFilterHiddenAndDirectory(File targetFile) {
        return targetFile.listFiles(pathname -> !pathname.isHidden() && pathname.isDirectory());
    }

    private void createBackupFile(File directory) {
        if (!this.sqlBackupStrategy.backup(directory)) {
            throw new SQLBackupException("Failed to create backup file");
        }
    }

    private void createBackupFileByDefault() {
        // Divide directories and create files from 0 number
        var childFileDirectory = this.getBackupChildFileDirectory(0);
        // Create a backup
        this.createBackupFile(childFileDirectory);
    }

    private File getBackupParentFileDirectory() {
        return Paths.get(this.properties.getHome(), this.properties.getDirectory())
                    .toFile();
    }

    private File getBackupDateFileDirectory() {
        var parentFileDirectory = this.getBackupParentFileDirectory();
        return Paths.get(parentFileDirectory.getAbsolutePath(), this.getDateDirectoryName())
                    .toFile();
    }

    private File getBackupChildFileDirectory(String parent, int index) {
        return Paths.get(parent, String.valueOf(index)).toFile();
    }

    private File getBackupChildFileDirectory(int index) {
        File backupDateFileDirectory = this.getBackupDateFileDirectory();
        return this.getBackupChildFileDirectory(backupDateFileDirectory.getAbsolutePath(), index);
    }

    private String getDateDirectoryName(){
        synchronized (this) {
            return DATE_FORMAT.format(new Date());
        }
    }

    private String getFileMD5(File file) {
        try {
            return DigestUtils.md5DigestAsHex(new FileInputStream(file));
        } catch (IOException e) {
            throw new SQLBackupException("Generate file MD5 error");
        }
    }

}
