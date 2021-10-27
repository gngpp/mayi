package com.zf1976.mayi.upms.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import static com.zf1976.mayi.common.core.util.DisableWarningUtil.disableWarning;

/**
 * @author mac
 * @date 2021/1/14
 **/
@SpringBootApplication(scanBasePackages = "com.zf1976")
@MapperScan(value = "com.zf1976", annotationClass = Repository.class)
@EnableDiscoveryClient
@EnableAsync
@EnableWebSocket
@EnableFeignClients(basePackages = "com.zf1976")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

}
