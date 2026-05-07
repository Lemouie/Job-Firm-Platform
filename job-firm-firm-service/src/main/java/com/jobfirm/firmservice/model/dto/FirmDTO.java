package com.jobfirm.firmservice.model.dto;

import lombok.Data;

/**
 * 事务所创建/更新请求参数
 */
@Data
public class FirmDTO {
    private String name;        // 事务所名称
    private String description; // 简介
    private String logoUrl;     // Logo地址
}
