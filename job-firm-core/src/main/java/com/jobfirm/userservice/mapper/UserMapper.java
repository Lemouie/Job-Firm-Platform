package com.jobfirm.userservice.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobfirm.userservice.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 接口
 * 继承 MyBatis-Plus BaseMapper，提供基础 CRUD 能力
 * 对应数据库表：user
 */
@Mapper
@DS("job_firm_core")
public interface UserMapper extends BaseMapper<User> {
}
