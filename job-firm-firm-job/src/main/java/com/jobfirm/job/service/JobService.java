package com.jobfirm.job.service;

import com.jobfirm.job.module.entity.Job;
import java.util.List;

/**
 * 差事表 Service 接口
 * 定义差事相关的业务方法
 */
public interface JobService {
    Job getById(Long id);                  // 根据ID查询差事
    List<Job> listAll();                   // 查询所有差事
    void createJob(Job job);               // 创建差事
    void updateJob(Job job);               // 更新差事
    void deleteJob(Long id);               // 删除差事
    void incrementOrderCount(Long jobId);  // 增加差事订单数量
}
