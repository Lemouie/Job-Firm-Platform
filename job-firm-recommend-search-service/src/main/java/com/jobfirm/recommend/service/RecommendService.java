package com.jobfirm.recommend.service;

import com.jobfirm.common.result.Result;

import java.util.List;
import java.util.Map;

public interface RecommendService {

    /** 推荐事务所（按订单数量排序） */
    Result<List<Map<String, Object>>> recommendFirms();

    /** 推荐差事（按订单数量排序，VIP优先） */
    Result<List<Map<String, Object>>> recommendJobs();

    /** 搜索事务所（按名称） */
    Result<List<Map<String, Object>>> searchFirms(String keyword);

    /** 搜索差事（按标题/关键词和分类） */
    Result<List<Map<String, Object>>> searchJobs(String keyword, String category);

    /** 健康检查 */
    Result<String> health();
}
