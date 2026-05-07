package com.jobfirm.firmservice.model.dto;

import lombok.Data;

/**
 * 事务申请/修改请求参数
 */
@Data
public class TaskDTO {
    private String title;       // 事务标题
    private String content;     // 事务内容
    private String category;    // 事务类别
}
