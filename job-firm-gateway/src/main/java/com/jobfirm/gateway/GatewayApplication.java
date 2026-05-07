package com.jobfirm.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//让 Spring 扫描 com.jobfirm 下的所有组件（跨模块生效）
@SpringBootApplication(scanBasePackages = "com.jobfirm")
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
