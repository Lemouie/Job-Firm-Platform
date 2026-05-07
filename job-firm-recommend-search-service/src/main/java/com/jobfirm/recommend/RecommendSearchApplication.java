package com.jobfirm.recommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.jobfirm")
public class RecommendSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecommendSearchApplication.class, args);
    }
}
