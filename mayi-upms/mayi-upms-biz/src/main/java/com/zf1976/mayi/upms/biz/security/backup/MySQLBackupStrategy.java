package com.zf1976.mayi.upms.biz.security.backup;

import com.zf1976.mayi.upms.biz.security.backup.exception.SQLBackupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

/**
 * 策略备份工具
 *
 * @author ant
 * Create by Ant on 2021/3/16 8:58 AM
 */
public class MySQLBackupStrategy implements SQLBackupStrategy {

    private final Logger log = LoggerFactory.getLogger("[SQL-BACKUP]");
    private final static String INDEX_END = ";";
    private final static String BLANK = "";
    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private final static int DEFAULT_BUFFER_SIZE = 16384;
    private final String mysqlDump;
    private final String mysqlRecover;
    private final Pattern pattern = Pattern.compile("(/)([a-zA-Z]*?)(\\?)");
    private final Pattern patternDefault = Pattern.compile("([/])([a-zA-Z]*)");
    private final DataSource dataSource;
    private final String database;

    public MySQLBackupStrategy(DataSource dataSource) {
        this.database = this.extractDatabase(dataSource);
        this.dataSource = dataSource;
        this.mysqlRecover = "mysql --defaults-extra-file=/etc/my.cnf " + this.getDatabase() + " < ";
        this.mysqlDump = "mysqldump --defaults-extra-file=/etc/my.cnf " + this.getDatabase();
    }

    /**
     * 提取URl数据库名
     *
     * @param dataSource 数据源
     * @return {@link String}
     * @date 2021-05-14 21:03:12
     */
    private String extractDatabase(DataSource dataSource) {
        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
            String url = connection.getMetaData()
                                   .getURL();
            String database;
            final Matcher matcher = this.pattern.matcher(url);
            // 第一次匹配URL
            while (matcher.find()) {
                database = matcher.group(2);
                if (database != null) {
                    return database;
                }
            }
            final Matcher matcherDefault = this.patternDefault.matcher(url);
            byte startIndex = 0;
            byte endIndex = 3;
            // 第二次匹配URL
            while (matcherDefault.find()) {
                ++startIndex;
                if (startIndex == endIndex) {
                    database = matcherDefault.group(2);
                    if (database != null) {
                        return database;
                    }
                }
            }
            throw new SQLBackupException("Cannot match data source name");
        } catch (SQLException e) {
            log.error("Invalid data source", e.getCause());
            throw new SQLBackupException("Invalid datasource", e.getCause());
        }
    }

    /**
     * 获取链接的数据库
     *
     * @return {@link String}
     * @date 2021-05-14 21:04:35
     */
    @Override
    public String getDatabase() {
        return this.database;
    }


    /**
     * 备份并生成文件
     *
     * @param fileDirectory 备份目录
     * @return {@link boolean}
     * @date 2021-05-14 18:13:11
     */
    @Override
    public boolean backup(File fileDirectory) {
        if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
            log.warn("Create a directory：{} failure", fileDirectory);
        }
        if (fileDirectory.isDirectory() || fileDirectory.exists()) {
            try {
                // 生成策略备份文件
                File backupFile = generationStrategyFile(fileDirectory);
                if (!backupFile.exists() || backupFile.isDirectory()) {
                    log.warn("Invalid backup file path：{}", fileDirectory.getAbsolutePath());
                    return false;
                }
                // 执行备份文件命令
                if (executeStrategyCommand(backupFile)) {
                    log.info("The {} Database backup is successful", getDatabase());
                }
                return true;
            } catch (IOException | InterruptedException exception) {
                log.error("Failed to backup database : {}", exception.getMessage());
                return false;
            }
        } else {
            log.warn("Directory：{} does not exist", fileDirectory);
        }
        return false;
    }

    @Override
    public boolean recover(File absolutePathFile) {
        if (absolutePathFile.exists() && absolutePathFile.canRead() && absolutePathFile.length() > 0) {
            String absolutePath = absolutePathFile.getAbsolutePath();
            try {
                Process p = Runtime.getRuntime()
                                   .exec(new String[]{"bash", "-c", mysqlRecover + absolutePath});
                if (p.waitFor() == 0) {
                    log.info("The database is restored successfully, data source:{}", absolutePath);
                    return true;
                } else {
                    log.info("Database recovery failed, data source:{}", absolutePath);
                }
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean recover(InputStream sqlInputStream) {
        try {
            List<String> sqlStatement = readFileByLines(sqlInputStream);
            if (sqlStatement.size() > 0) {
                int num = batchSql(sqlStatement);
                if (num > 0) {
                    log.info("Execute complete...");
                    return false;
                } else {
                    log.info("No execute sqlStatement...");
                    return true;
                }
            } else {
                log.info("No execute sqlStatement...");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 执行备份文件命令
     *
     * @param backupFile 备份文件对象
     * @return {@link boolean}
     * @date 2021-05-14 18:14:46
     */
    private boolean executeStrategyCommand(File backupFile) throws IOException, InterruptedException {
        Process exec = Runtime.getRuntime()
                              .exec(mysqlDump);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(exec.getInputStream(), DEFAULT_BUFFER_SIZE);
        if (!this.writeBackupFile(bufferedInputStream, backupFile)) {
            log.warn("Failed to write backup file, file:{}", backupFile.getName());
            return false;
        }
        return exec.waitFor() == 0;
    }

    /**
     * 写入备份文件
     *
     * @param bufferedReader 缓冲流
     * @param backupFile     备份文件
     * @return boolean
     * @date 2021-03-21 14:17:22
     */
    private boolean writeBackupFile(BufferedInputStream bufferedReader, File backupFile) {
        if (bufferedReader == null) {
            log.warn("Invalid backup file command:" + mysqlDump);
            return false;
        }
        try (bufferedReader; BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(backupFile), DEFAULT_BUFFER_SIZE);
             var gzipOutputStream = new GZIPOutputStream(outputStream);
        ) {
            byte[] data = new byte[DEFAULT_BUFFER_SIZE];
            int len;
            while ((len = bufferedReader.read(data)) != -1) {
                gzipOutputStream.write(data, 0, len);
            }
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }


    /**
     * @param sql 包含待执行的SQL语句的ArrayList集合
     * @return int 影响的函数
     */
    private int batchSql(List<String> sql) {
        try (Connection connection = this.dataSource.getConnection();
             Statement st = connection.createStatement()) {
            // 进行手动提交
            connection.setAutoCommit(false);
            for (String subSql : sql) {
                st.addBatch(subSql);
            }
            try {
                st.executeBatch();
            } catch (SQLException e) {
                // 执行失败回滚
                connection.rollback();
                throw e;
            }
            return 1;
        } catch (Exception e) {
            log.error("Batch SQL task failed", e.getCause());
            return 0;
        }
    }

    /**
     * 根据策略生成备份文件
     *
     * @param directory 文件目录
     * @return file
     */
    private File generationStrategyFile(File directory) throws IOException {
        File file = Paths.get(directory.getAbsolutePath(), this.generatorFilename())
                         .toFile();
        if (!file.createNewFile()) {
            throw new SQLBackupException("Failed to create file：" + file.getName());
        }
        return file;
    }

    /**
     * 以行为单位读取文件，并将文件的每一行格式化到ArrayList中，常用于读面向行的格式化文件
     *
     * @param sqlInputStream sql文件流
     * @return {@link List<String>}
     * @date 2021-05-14 18:30:54
     */
    private List<String> readFileByLines(InputStream sqlInputStream) throws Exception {
        List<String> sqlList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(sqlInputStream, StandardCharsets.UTF_8))) {
            String tempSql;
            int flag = 0;
            // 一次读入一行，直到读入null为文件结束
            while ((tempSql = reader.readLine()) != null) {
                // 非空白继续执行
                if (!Objects.equals(BLANK, tempSql.trim())) {
                    flag = handlerFlag(sqlList, sb, tempSql, flag);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return sqlList;
    }

    /**
     * sql处理标记
     *
     * @param sqlList sql列表
     * @param sb      string builder
     * @param tempSql sql模版
     * @param flag    标记
     * @return {@link int}
     * @date 2021-05-14 18:45:27
     */
    public int handlerFlag(List<String> sqlList, StringBuffer sb, String tempSql, int flag) {
        if (INDEX_END.equals(tempSql.substring(tempSql.length() - 1))) {
            if (flag == 1) {
                sb.append(tempSql);
                sqlList.add(sb.toString());
                sb.delete(0, sb.length());
                flag = 0;
            } else {
                sqlList.add(tempSql);
            }
        } else {
            flag = 1;
            sb.append(tempSql);
        }
        return flag;
    }

    /**
     * 生成文件名
     *
     * @return {@link String}
     * @date 2021-05-14 21:46:03
     */
    private String generatorFilename() {
        final var now = LocalDateTime.now();
        // 加上时间戳保证生成文件唯一性，失败前提是执行一次时间极短
        return this.database + "-backup_" + now.format(DATE_FORMAT) + "_" + now.toInstant(ZoneOffset.of("+8"))
                                                                               .toEpochMilli() + ".sql";
    }

}
