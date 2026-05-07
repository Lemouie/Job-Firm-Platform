package com.jobfirm.infrastructure.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sentinel 核心配置
 * <p>
 * 1. 注册 SentinelResourceAspect 以启用 @SentinelResource 注解支持
 * 2. 使用 SentinelRulesConfig 初始化流控规则
 */
@Configuration
public class SentinelConfig {

    /**
     * 注册 Sentinel 注解切面
     * <p>
     * 使 @SentinelResource 注解生效，支持在方法级别定义限流、降级规则。
     * 需要 sentinel-annotation-aspectj 依赖。
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}
