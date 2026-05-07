package com.jobfirm.userservice.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jobfirm.infrastructure.entity.BaseEntity;
import lombok.Data;

/**
 * 用户实体类
 * 对应数据库表：user
 * 继承 BaseEntity（createdAt、updatedAt）
 */
@Data
@TableName("user")
public class User extends BaseEntity {

    /** 用户ID（主键，自增） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 用户昵称 */
    private String username;

    /** 手机号（唯一） */
    private String phone;

    /** 邮箱（唯一） */
    private String email;

    /** 密码（加密存储） */
    private String password;

    /** 头像地址 */
    private String avatarUrl;

    /** 用户角色：CUSTOMER / CEO / ADMIN */
    private String role;

    /** 用户状态：ACTIVE / DISABLED */
    private String status;
}
