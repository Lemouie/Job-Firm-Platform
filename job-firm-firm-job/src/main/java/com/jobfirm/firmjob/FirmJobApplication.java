package com.jobfirm.firmjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.jobfirm")
@EnableFeignClients(basePackages = "com.jobfirm")
public class FirmJobApplication {
    public static void main(String[] args) {
        SpringApplication.run(FirmJobApplication.class, args);
    }
}
