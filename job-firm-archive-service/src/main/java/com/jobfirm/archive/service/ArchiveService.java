package com.jobfirm.archive.service;

import com.jobfirm.archive.model.entity.ArchiveRecord;
import com.jobfirm.common.result.Result;

import java.util.List;

public interface ArchiveService {

    /** 手动触发归档 */
    Result<Void> executeArchive(String archiveType, String archiveMonth);

    /** 查询归档记录列表 */
    Result<List<ArchiveRecord>> listRecords();

    /** 获取归档记录的OSS下载路径 */
    Result<String> getDownloadPath(Long id);

    /** 健康检查 */
    Result<String> health();
}
