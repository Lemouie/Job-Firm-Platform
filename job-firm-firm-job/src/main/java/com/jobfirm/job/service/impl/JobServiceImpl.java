package com.jobfirm.job.service.impl;

import com.jobfirm.common.exception.BusinessException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.job.module.entity.Job;
import com.jobfirm.job.mapper.JobMapper;
import com.jobfirm.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 差事表 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobMapper jobMapper;

    @Override
    public Job getById(Long id) {
        Job job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND);
        }
        return job;
    }

    @Override
    public List<Job> listAll() {
        return jobMapper.selectList(null);
    }

    @Override
    public void createJob(Job job) {
        if (job.getTitle() == null || job.getTitle().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "差事标题不能为空");
        }
        if (job.getFirmId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "事务所ID不能为空");
        }
        job.setOrderCount(0);
        job.setStatus("PENDING");
        jobMapper.insert(job);
    }

    @Override
    public void updateJob(Job job) {
        Job existing = jobMapper.selectById(job.getId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND);
        }
        jobMapper.updateById(job);
    }

    @Override
    public void deleteJob(Long id) {
        Job existing = jobMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND);
        }
        jobMapper.deleteById(id);
    }

    @Override
    public void incrementOrderCount(Long jobId) {
        Job job = jobMapper.selectById(jobId);
        if (job == null) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND);
        }
        job.setOrderCount(job.getOrderCount() == null ? 1 : job.getOrderCount() + 1);
        jobMapper.updateById(job);
    }
}
