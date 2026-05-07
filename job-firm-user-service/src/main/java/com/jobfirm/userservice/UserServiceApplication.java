package com.jobfirm.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
/**
 * 用户服务启动类
 * 启用 Nacos 服务发现 + Feign
 */
//让 Spring 扫描 com.jobfirm 下的所有组件（跨模块生效）
@SpringBootApplication(scanBasePackages = "com.jobfirm")
// 开启Feign接口扫描，让你可以用接口（默认模块内FeignClient）调用其他模块服务。
// pom.xml要导入spring-cloud-starter-openfeign和要使用的其他模块-api。
@EnableFeignClients(basePackages = "com.jobfirm")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
