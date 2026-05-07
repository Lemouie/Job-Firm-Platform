package com.jobfirm.userservice.model.vo;

import lombok.Data;

/**
 * 用户信息 VO
 * 用于返回给前端的用户数据
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String avatarUrl;
    private String role;
    private String status;
}
