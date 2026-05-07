package com.jobfirm.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * 用户服务启动类
 */
//让 Spring 扫描 com.jobfirm 下的所有组件（跨模块生效）
@SpringBootApplication(scanBasePackages = "com.jobfirm")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
