package com.jobfirm.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobfirm.admin.model.entity.AdminActionLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("job_firm_management")
public interface AdminActionLogMapper extends BaseMapper<AdminActionLog> {
}
