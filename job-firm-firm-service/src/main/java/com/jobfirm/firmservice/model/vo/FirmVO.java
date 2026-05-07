package com.jobfirm.firmservice.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 事务所展示对象
 */
@Data
public class FirmVO {
    private Long id;
    private Long ceoId;
    private String name;
    private String description;
    private String logoUrl;
    private String status;        // 审核状态
    private String vipStatus;     // VIP状态
    private Date vipExpireTime;
    private BigDecimal revenue;   // 总收入
    private Date createdAt;
}
