package com.jobfirm.archive.controller;

import com.jobfirm.archive.model.entity.ArchiveRecord;
import com.jobfirm.archive.service.ArchiveService;
import com.jobfirm.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/archive")
@RequiredArgsConstructor
public class ArchiveController {

    private final ArchiveService archiveService;

    /** 健康检查 */
    @GetMapping("/health")
    public Result<String> health() {
        return archiveService.health();
    }

    /** 手动触发归档 */
    @PostMapping("/execute")
    public Result<Void> executeArchive(
            @RequestParam String archiveType,
            @RequestParam String archiveMonth) {
        return archiveService.executeArchive(archiveType, archiveMonth);
    }

    /** 查询归档记录列表 */
    @GetMapping("/records")
    public Result<List<ArchiveRecord>> listRecords() {
        return archiveService.listRecords();
    }

    /** 获取归档记录的OSS下载路径 */
    @GetMapping("/records/{id}/download")
    public Result<String> getDownloadPath(@PathVariable Long id) {
        return archiveService.getDownloadPath(id);
    }
}
