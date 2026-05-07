package com.jobfirm.job.service;

import com.jobfirm.job.module.entity.JobImage;
import java.util.List;

/**
 * 差事图片表 Service 接口
 * 定义差事图片相关的业务方法
 */
public interface JobImageService {
    List<JobImage> listByJobId(Long jobId);   // 根据差事ID查询图片
    void saveImages(JobImage jobImage);       // 保存图片记录
    void deleteByJobId(Long jobId);           // 删除差事对应的图片
}
