package com.jobfirm.archive.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobfirm.archive.model.entity.ArchiveRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("job_firm_management")
public interface ArchiveRecordMapper extends BaseMapper<ArchiveRecord> {
}
