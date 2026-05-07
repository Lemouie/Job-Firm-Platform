package com.jobfirm.job.controller;


import com.jobfirm.job.module.dto.JobImageDTO;
import com.jobfirm.job.module.entity.JobImage;
import com.jobfirm.job.service.JobImageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * 差事图片表 Controller
 * 提供差事图片相关的 RESTful API
 */
@RestController
@RequestMapping("/api/job-images")
public class JobImageController {

    @Resource
    private JobImageService jobImageService;

    /**
     * 根据差事ID查询图片列表
     */
    @GetMapping("/{jobId}")
    public List<JobImage> listImages(@PathVariable Long jobId) {
        return jobImageService.listByJobId(jobId);
    }

    /**
     * 保存图片记录
     */
    @PostMapping
    public void saveImages(@RequestBody JobImageDTO dto) {
        JobImage jobImage = new JobImage();
        jobImage.setJobId(dto.getJobId());
        // 将图片列表映射到 imageUrl1~9
        List<String> images = dto.getImages();
        if (images != null) {
            if (images.size() > 0) jobImage.setImageUrl1(images.get(0));
            if (images.size() > 1) jobImage.setImageUrl2(images.get(1));
            if (images.size() > 2) jobImage.setImageUrl3(images.get(2));
            if (images.size() > 3) jobImage.setImageUrl4(images.get(3));
            if (images.size() > 4) jobImage.setImageUrl5(images.get(4));
            if (images.size() > 5) jobImage.setImageUrl6(images.get(5));
            if (images.size() > 6) jobImage.setImageUrl7(images.get(6));
            if (images.size() > 7) jobImage.setImageUrl8(images.get(7));
            if (images.size() > 8) jobImage.setImageUrl9(images.get(8));
        }
        jobImageService.saveImages(jobImage);
    }

    /**
     * 删除差事对应的图片
     */
    @DeleteMapping("/{jobId}")
    public void deleteImages(@PathVariable Long jobId) {
        jobImageService.deleteByJobId(jobId);
    }
}
