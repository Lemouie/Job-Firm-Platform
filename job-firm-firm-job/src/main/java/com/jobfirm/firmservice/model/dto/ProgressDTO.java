package com.jobfirm.firmservice.model.dto;

import lombok.Data;

/**
 * 订单进度更新请求参数
 */
@Data
public class ProgressDTO {
    private String progressInfo; // 进度描述
    private Integer percent;     // 完成百分比
}
