package com.jobfirm.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobfirm.job.module.entity.Job;
import org.apache.ibatis.annotations.Mapper;

/**
 * 差事表 Mapper 接口
 * 提供基础的 CRUD 操作
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {
}