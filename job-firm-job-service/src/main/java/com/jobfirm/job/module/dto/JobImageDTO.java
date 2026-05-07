package com.jobfirm.job.module.dto;

import lombok.Data;
import java.util.List;

/**
 * 差事图片请求 DTO
 * 用于接收前端上传的图片列表
 */
@Data
public class JobImageDTO {
    private Long jobId;             // 差事ID
    private List<String> images;    // 图片列表（最多9张）
}
