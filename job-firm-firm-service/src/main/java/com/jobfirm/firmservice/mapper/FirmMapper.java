package com.jobfirm.firmservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobfirm.firmservice.model.entity.Firm;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FirmMapper extends BaseMapper<Firm> {
}
