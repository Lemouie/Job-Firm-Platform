package com.jobfirm.job.module.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jobfirm.infrastructure.entity.BaseEntity;
import lombok.Data;

/**
 * 差事图片表实体类
 * 对应表：job_image
 */
@Data
@TableName("job_image")
public class JobImage extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;                // 主键ID，雪花算法生成
    private Long jobId;             // 差事ID（关联 job 表）
    private String imageUrl1;       // 图片1地址，默认NULL
    private String imageUrl2;       // 图片2地址，默认NULL
    private String imageUrl3;       // 图片3地址，默认NULL
    private String imageUrl4;       // 图片4地址，默认NULL
    private String imageUrl5;       // 图片5地址，默认NULL
    private String imageUrl6;       // 图片6地址，默认NULL
    private String imageUrl7;       // 图片7地址，默认NULL
    private String imageUrl8;       // 图片8地址，默认NULL
    private String imageUrl9;       // 图片9地址，默认NULL
}
