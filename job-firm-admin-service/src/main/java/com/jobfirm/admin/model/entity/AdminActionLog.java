package com.jobfirm.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin_action_log")
public class AdminActionLog {

    @TableId
    private Long id;

    /** 管理员ID */
    private Long adminId;

    /** 操作类型：disable_user, approve_firm, reject_firm, disable_firm, adjudicate_order, archive_execute 等 */
    private String actionType;

    /** 操作目标ID（用户ID、事务所ID、订单ID等） */
    private Long targetId;

    /** 操作描述 */
    private String description;

    /** 操作结果：success / fail */
    private String result;

    /** 操作详情JSON */
    private String detail;

    /** 创建时间 */
    private LocalDateTime createdTime;
}
