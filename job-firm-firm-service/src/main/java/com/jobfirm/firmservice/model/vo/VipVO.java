package com.jobfirm.firmservice.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * VIP状态展示对象
 */
@Data
public class VipVO {
    private String vipStatus;     // NONE, ACTIVE, EXPIRED
    private Date vipExpireTime;   // 到期时间
}
