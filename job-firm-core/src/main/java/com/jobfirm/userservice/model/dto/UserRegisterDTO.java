package com.jobfirm.userservice.model.dto;

import lombok.Data;

/**
 * 用户注册 DTO
 */
@Data
public class UserRegisterDTO {
    private String username;
    private String phone;
    private String email;
    private String password;
    private String role;
}
