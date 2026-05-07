package com.jobfirm.firmservice.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jobfirm.infrastructure.entity.BaseEntity;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("firm")
public class Firm extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long ceoId;
    private String name;
    private String description;
    private String logoUrl;
    private Integer escapeCount;
    private String status;        // PENDING, APPROVED, REJECTED
    private String vipStatus;     // NONE, ACTIVE, EXPIRED
    private Date vipExpireTime;
    private BigDecimal revenue;   // 总收入
}
