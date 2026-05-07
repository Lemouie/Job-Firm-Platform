package com.jobfirm.userservice.config;

import com.jobfirm.common.config.JobFirmProperties;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {  // 使用openfeign调用job-firm-api模块的Client时，为转发的request，添加内部调用密钥

    @Bean
    public RequestInterceptor requestInterceptor(JobFirmProperties properties) {
        return template -> {
            template.header("X-Internal-Secret", properties.getInternalSecret());
        };
    }
}
