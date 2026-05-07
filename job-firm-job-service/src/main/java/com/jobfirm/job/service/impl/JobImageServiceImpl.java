package com.jobfirm.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jobfirm.job.module.entity.JobImage;
import com.jobfirm.job.mapper.JobImageMapper;
import com.jobfirm.job.service.JobImageService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 差事图片表 Service 实现类
 */
@Service
public class JobImageServiceImpl implements JobImageService {

    @Resource
    private JobImageMapper jobImageMapper;

    @Override
    public List<JobImage> listByJobId(Long jobId) {
        return jobImageMapper.selectList(
                new QueryWrapper<JobImage>()
                        .eq("job_id", jobId)
        );
    }

    @Override
    public void saveImages(JobImage jobImage) {
        jobImageMapper.insert(jobImage);
    }

    @Override
    public void deleteByJobId(Long jobId) {
        jobImageMapper.delete(
                new QueryWrapper<JobImage>()
                        .eq("job_id", jobId)
        );
    }
}
