package com.jobfirm.firmservice.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobfirm.firmservice.model.entity.Firm;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("job_firm_firm")
public interface FirmMapper extends BaseMapper<Firm> {
}
