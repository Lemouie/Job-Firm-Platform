package com.jobfirm.archive.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("archive_record")
public class ArchiveRecord {

    @TableId
    private Long id;

    /** 归档类型：order_archive, payment_archive 等 */
    private String archiveType;

    /** 归档月份（格式：yyyy-MM） */
    private String archiveMonth;

    /** OSS存储路径 */
    private String ossPath;

    /** 归档记录数 */
    private Integer recordCount;

    /** 状态：pending, processing, completed, failed */
    private String status;

    /** 失败原因 */
    private String failReason;

    /** 创建时间 */
    private LocalDateTime createdTime;

    /** 更新时间 */
    private LocalDateTime updatedTime;
}
