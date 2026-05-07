package com.jobfirm.infrastructure.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

// 启用 Nacos 服务发现（Spring Cloud 2023.x 必须显式配置）
@Configuration
@EnableDiscoveryClient
public class NacosDiscoveryConfig {
}
