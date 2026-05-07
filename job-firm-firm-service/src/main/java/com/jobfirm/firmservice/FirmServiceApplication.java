package com.jobfirm.firmservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.jobfirm")
public class FirmServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FirmServiceApplication.class, args);
    }
}
