package com.jobfirm.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jobfirm.archive.mapper.ArchiveRecordMapper;
import com.jobfirm.archive.model.entity.ArchiveRecord;
import com.jobfirm.archive.service.ArchiveService;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveRecordMapper archiveRecordMapper;

    @Override
    public Result<Void> executeArchive(String archiveType, String archiveMonth) {
        log.info("Execute archive: type={}, month={}", archiveType, archiveMonth);

        if (archiveType == null || archiveType.isBlank()) {
            return Result.fail(ErrorCode.PARAM_ERROR.getCode(), "归档类型不能为空");
        }
        if (archiveMonth == null || archiveMonth.isBlank()) {
            return Result.fail(ErrorCode.PARAM_ERROR.getCode(), "归档月份不能为空");
        }

        // Create archive record
        ArchiveRecord record = new ArchiveRecord();
        record.setArchiveType(archiveType);
        record.setArchiveMonth(archiveMonth);
        record.setStatus("pending");
        record.setCreatedTime(LocalDateTime.now());
        record.setUpdatedTime(LocalDateTime.now());
        archiveRecordMapper.insert(record);

        // Simulate archive process: select old orders/payments and mark them
        try {
            record.setStatus("processing");
            archiveRecordMapper.updateById(record);

            // TODO: Real archive logic - Feign calls to order-service / payment-service
            // Select records before archiveMonth and copy to archive storage / OSS
            log.info("Simulating archive for type={}, month={}...", archiveType, archiveMonth);

            // Simulate processing time
            Thread.sleep(500);

            // Success
            record.setStatus("completed");
            record.setOssPath("/archive/" + archiveType + "/" + archiveMonth + "/data.json");
            record.setRecordCount(0); // Will be set when real logic is implemented
            record.setUpdatedTime(LocalDateTime.now());
            archiveRecordMapper.updateById(record);

            log.info("Archive completed: type={}, month={}", archiveType, archiveMonth);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Archive failed: type={}, month={}", archiveType, archiveMonth, e);
            record.setStatus("failed");
            record.setFailReason(e.getMessage());
            record.setUpdatedTime(LocalDateTime.now());
            archiveRecordMapper.updateById(record);
            return Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), "归档执行失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ArchiveRecord>> listRecords() {
        log.info("List archive records");
        LambdaQueryWrapper<ArchiveRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ArchiveRecord::getCreatedTime);
        List<ArchiveRecord> records = archiveRecordMapper.selectList(wrapper);
        return Result.success(records);
    }

    @Override
    public Result<String> getDownloadPath(Long id) {
        log.info("Get download path for archive record: {}", id);
        ArchiveRecord record = archiveRecordMapper.selectById(id);
        if (record == null) {
            return Result.fail(ErrorCode.PARAM_ERROR.getCode(), "归档记录不存在");
        }
        if (!"completed".equals(record.getStatus())) {
            return Result.fail(ErrorCode.PARAM_ERROR.getCode(), "归档记录未完成，无法下载");
        }
        return Result.success(record.getOssPath());
    }

    @Override
    public Result<String> health() {
        return Result.success("Archive Service is running");
    }
}
