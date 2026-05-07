package com.jobfirm.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;  // token加密密钥
    private Long expire; // 过期时间，单位：秒
    private String tokenCachePrefix;  // token的redis存储前缀
}
