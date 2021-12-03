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

package com.zf1976.mayi.upms.biz.security.controller;

import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.upms.biz.security.backup.service.MySQLBackupService;
import com.zf1976.mayi.upms.biz.security.pojo.BackupFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ant
 * Create by Ant on 2021/6/8 5:57 上午
 */
@RestController
@RequestMapping(
        value = "/api/security/backup"
)
public class SQLBackupController {

    private final MySQLBackupService mySQLBackupService;

    public SQLBackupController(MySQLBackupService mySQLBackupService) {
        this.mySQLBackupService = mySQLBackupService;
    }

    @PreAuthorize("hasRole('root')")
    @PostMapping("/select/files")
    DataResult<List<BackupFile>> selectBackupList(@RequestParam @NotEmpty String date, @RequestParam @NotNull Integer page) {
        return DataResult.success(this.mySQLBackupService.selectBackupFileByDate(date, page));
    }

    @PreAuthorize("hasRole('root')")
    @GetMapping("/select/dates")
    DataResult<List<String>> selectDateList() {
        return DataResult.success(this.mySQLBackupService.selectBackupDate());
    }

    @PreAuthorize("hasRole('root')")
    @PostMapping("/create")
    DataResult<Void> createBackup(){
        return DataResult.success(this.mySQLBackupService.createBackup());
    }

    @PreAuthorize("hasRole('root')")
    @DeleteMapping("/delete")
    DataResult<Void> deleteBackupFile(@RequestParam @NotEmpty String filename) {
        return DataResult.success(this.mySQLBackupService.deleteBackupFileByFilename(filename));
    }

}
