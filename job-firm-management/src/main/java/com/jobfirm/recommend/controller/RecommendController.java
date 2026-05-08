package com.jobfirm.recommend.controller;

import com.jobfirm.common.result.Result;
import com.jobfirm.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    /** 健康检查 */
    @GetMapping("/health")
    public Result<String> health() {
        return recommendService.health();
    }

    /** 推荐事务所（按订单数量排序） */
    @GetMapping("/recommend/firms")
    public Result<List<Map<String, Object>>> recommendFirms() {
        return recommendService.recommendFirms();
    }

    /** 推荐差事（按订单数量排序，VIP优先） */
    @GetMapping("/recommend/jobs")
    public Result<List<Map<String, Object>>> recommendJobs() {
        return recommendService.recommendJobs();
    }

    /** 搜索事务所（按名称） */
    @GetMapping("/search/firms")
    public Result<List<Map<String, Object>>> searchFirms(@RequestParam String keyword) {
        return recommendService.searchFirms(keyword);
    }

    /** 搜索差事（按标题/关键词和分类） */
    @GetMapping("/search/jobs")
    public Result<List<Map<String, Object>>> searchJobs(
            @RequestParam String keyword,
            @RequestParam(required = false) String category) {
        return recommendService.searchJobs(keyword, category);
    }
}
