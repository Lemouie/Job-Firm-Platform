package com.jobfirm.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jobfirm")
public class JobFirmProperties {

    private String internalSecret;

}