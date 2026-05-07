package com.jobfirm.api.config;

import com.jobfirm.common.config.JobFirmProperties;
import com.jobfirm.common.result.Result;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

/**
 * 默认 Feign 配置
 * 为所有服务间调用添加内部密钥及请求ID头
 */
public class DefaultFeignConfig {

    private static final Logger log = LoggerFactory.getLogger(DefaultFeignConfig.class);

    /**
     * 内部密钥拦截器
     * 为每个 Feign 请求添加 X-Internal-Secret 和 X-Request-Id 头
     */
    @Bean
    public RequestInterceptor internalSecretInterceptor(JobFirmProperties properties) {
        return template -> {
            template.header("X-Internal-Secret", properties.getInternalSecret());
            template.header("X-Request-Id", UUID.randomUUID().toString());
        };
    }

    /**
     * 默认错误解码器
     * 将 Feign 异常响应包装为 Result<T> 格式
     */
    @Bean
    public ErrorDecoder defaultErrorDecoder() {
        return (methodKey, response) -> {
            int status = response.status();
            log.error("Feign call [{}] failed with status {}: {}", methodKey, status, response.reason());

            // 返回一个可识别的业务异常，上层可统一处理
            return new FeignClientException(status, "Feign call failed: " + methodKey);
        };
    }

    /**
     * Feign 调用自定义异常
     */
    public static class FeignClientException extends RuntimeException {
        private final int status;

        public FeignClientException(int status, String message) {
            super(message);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}
