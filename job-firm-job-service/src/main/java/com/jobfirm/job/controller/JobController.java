package com.jobfirm.job.controller;


import com.jobfirm.common.result.Result;
import com.jobfirm.job.enums.CategoryEnum;
import com.jobfirm.job.module.dto.JobCreateDTO;
import com.jobfirm.job.module.entity.Job;
import com.jobfirm.job.module.vo.JobDetailVO;
import com.jobfirm.job.module.vo.JobVO;
import com.jobfirm.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 差事表 Controller
 * 提供差事相关的 RESTful API
 */
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /**
     * 根据ID查询差事详情
     */
    @GetMapping("/{id}")
    public Result<JobDetailVO> getJobDetail(@PathVariable Long id) {
        Job job = jobService.getById(id);
        JobDetailVO vo = new JobDetailVO();
        vo.setId(job.getId());
        vo.setTitle(job.getTitle());
        vo.setDescription(job.getDescription());
        vo.setCategory(job.getCategory().name());
        vo.setIsVip(job.getIsVip());
        vo.setPrice(job.getPrice());
        vo.setOrderCount(job.getOrderCount());
        vo.setStatus(job.getStatus());
        // 图片和事务所信息可通过其他服务补充
        return Result.success(vo);
    }

    /**
     * 查询所有差事列表
     */
    @GetMapping
    public Result<List<JobVO>> listJobs() {
        List<JobVO> vos = jobService.listAll().stream().map(job -> {
            JobVO vo = new JobVO();
            vo.setId(job.getId());
            vo.setTitle(job.getTitle());
            vo.setCategory(job.getCategory().name());
            vo.setIsVip(job.getIsVip());
            vo.setPrice(job.getPrice());
            vo.setOrderCount(job.getOrderCount());
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    /**
     * 创建差事
     */
    @PostMapping
    public Result<Void> createJob(@RequestBody JobCreateDTO dto) {
        Job job = new Job();
        job.setFirmId(dto.getFirmId());
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setCategory(CategoryEnum.valueOf(dto.getCategory()));
        job.setIsVip(dto.getIsVip());
        job.setPrice(dto.getPrice());
        jobService.createJob(job);
        return Result.success(null);
    }

    /**
     * 更新差事
     */
    @PutMapping("/{id}")
    public Result<Void> updateJob(@PathVariable Long id, @RequestBody JobCreateDTO dto) {
        Job job = new Job();
        job.setId(id);
        job.setFirmId(dto.getFirmId());
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setCategory(CategoryEnum.valueOf(dto.getCategory()));
        job.setIsVip(dto.getIsVip());
        job.setPrice(dto.getPrice());
        jobService.updateJob(job);
        return Result.success(null);
    }

    /**
     * 删除差事
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return Result.success(null);
    }
}
