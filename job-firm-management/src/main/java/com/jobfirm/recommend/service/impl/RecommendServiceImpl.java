package com.jobfirm.recommend.service.impl;

import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.common.result.Result;
import com.jobfirm.recommend.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RecommendServiceImpl implements RecommendService {

    // In-memory cache simulation (would be Redis in production)
    private List<Map<String, Object>> cachedFirms = null;
    private List<Map<String, Object>> cachedJobs = null;
    private long lastFirmCacheTime = 0;
    private long lastJobCacheTime = 0;
    private static final long CACHE_TTL = 60000; // 60 seconds

    @Override
    public Result<List<Map<String, Object>>> recommendFirms() {
        log.info("Recommend firms - by order count");
        // Check in-memory cache
        if (cachedFirms != null && (System.currentTimeMillis() - lastFirmCacheTime) < CACHE_TTL) {
            log.debug("Returning cached firm recommendations");
            return Result.success(cachedFirms);
        }

        // TODO: Feign call to firm-service / order-service for real data
        log.warn("Feign call to firm-service not yet implemented. Returning empty recommendation list.");
        cachedFirms = Collections.emptyList();
        lastFirmCacheTime = System.currentTimeMillis();
        return Result.success(cachedFirms);
    }

    @Override
    public Result<List<Map<String, Object>>> recommendJobs() {
        log.info("Recommend jobs - by order count, VIP first");
        // Check in-memory cache
        if (cachedJobs != null && (System.currentTimeMillis() - lastJobCacheTime) < CACHE_TTL) {
            log.debug("Returning cached job recommendations");
            return Result.success(cachedJobs);
        }

        // TODO: Feign call to job-service / order-service for real data
        log.warn("Feign call to job-service not yet implemented. Returning empty recommendation list.");
        cachedJobs = Collections.emptyList();
        lastJobCacheTime = System.currentTimeMillis();
        return Result.success(cachedJobs);
    }

    @Override
    public Result<List<Map<String, Object>>> searchFirms(String keyword) {
        log.info("Search firms by keyword: {}", keyword);
        if (keyword == null || keyword.isBlank()) {
            return Result.fail(ErrorCode.PARAM_ERROR.getCode(), "关键词不能为空");
        }
        // TODO: Feign call to firm-service
        log.warn("Feign call to firm-service not yet implemented. Returning empty search result for: {}", keyword);
        return Result.success(Collections.emptyList());
    }

    @Override
    public Result<List<Map<String, Object>>> searchJobs(String keyword, String category) {
        log.info("Search jobs by keyword: {}, category: {}", keyword, category);
        if (keyword == null || keyword.isBlank()) {
            return Result.fail(ErrorCode.PARAM_ERROR.getCode(), "关键词不能为空");
        }
        // TODO: Feign call to job-service
        log.warn("Feign call to job-service not yet implemented. Returning empty search result for: {}", keyword);
        return Result.success(Collections.emptyList());
    }

    @Override
    public Result<String> health() {
        return Result.success("RecommendSearch Service is running");
    }
}
