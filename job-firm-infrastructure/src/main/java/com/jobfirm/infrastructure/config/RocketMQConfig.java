package com.jobfirm.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ 配置类
 * 提供 RocketMQ 相关的全局配置 Bean
 */
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
public class RocketMQConfig {

    /**
     * 用于消息序列化的 ObjectMapper
     * 注册了 JavaTimeModule 以支持 Java 8 时间类型
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper rocketMQObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
