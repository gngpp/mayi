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

package com.gngpp.mayi.upms.biz.security.backup.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ant
 * Create by Ant on 2021/6/8 4:58 上午
 */
@Component
@ConfigurationProperties(prefix = "sql-backup")
public class SQLBackupProperties {

    private String home;

    private String directory;

    private Integer dayTotal;

    private Integer fileCountSize;

    public Integer getFileCountSize() {
        return fileCountSize;
    }

    public SQLBackupProperties setFileCountSize(Integer fileCountSize) {
        this.fileCountSize = fileCountSize;
        return this;
    }

    public Integer getDayTotal() {
        return dayTotal;
    }

    public SQLBackupProperties setDayTotal(Integer dayTotal) {
        this.dayTotal = dayTotal;
        return this;
    }

    public String getHome() {
        return home;
    }

    public SQLBackupProperties setHome(String home) {
        this.home = System.getProperty(home);
        return this;
    }

    public String getDirectory() {
        return directory;
    }

    public SQLBackupProperties setDirectory(String directory) {
        this.directory = directory;
        return this;
    }
}
