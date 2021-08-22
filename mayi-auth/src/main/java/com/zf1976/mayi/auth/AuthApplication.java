package com.zf1976.mayi.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

import static com.zf1976.mayi.common.core.util.DisableWarningUtil.disableWarning;

/**
 * @author mac
 * @date 2021/2/10
 **/
@SpringBootApplication(scanBasePackages = "com.zf1976")
@EnableDiscoveryClient
@EnableAsync
@EnableFeignClients(basePackageClasses = RemoteUserService.class)
@EnableConfigurationProperties
public class AuthApplication {

    public static void main(String[] args) {
        disableWarning();
        SpringApplication.run(AuthApplication.class, args);
    }

}
