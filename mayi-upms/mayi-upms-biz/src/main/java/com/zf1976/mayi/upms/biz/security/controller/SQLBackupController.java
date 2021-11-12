package com.zf1976.mayi.upms.biz.security.controller;

import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.upms.biz.security.backup.service.MySQLBackupService;
import com.zf1976.mayi.upms.biz.security.pojo.BackupFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/select/files")
    DataResult<List<BackupFile>> selectBackupList(@RequestParam String date, @RequestParam Integer page) {
        return DataResult.success(this.mySQLBackupService.selectBackupFileByDate(date, page));
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/select/dates")
    DataResult<List<String>> selectDateList() {
        return DataResult.success(this.mySQLBackupService.selectBackupDate());
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/create")
    DataResult<Void> createBackup(){
        return DataResult.success(this.mySQLBackupService.createBackup());
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/delete")
    DataResult<Void> deleteBackupFile(@RequestParam String filename) {
        return DataResult.success(this.mySQLBackupService.deleteBackupFileByFilename(filename));
    }

}
