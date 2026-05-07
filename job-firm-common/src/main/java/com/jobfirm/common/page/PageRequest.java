package com.jobfirm.common.page;

import lombok.Data;

// 分页请求 DTO（后续 order-service、user-service 都会用）
@Data
public class PageRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
