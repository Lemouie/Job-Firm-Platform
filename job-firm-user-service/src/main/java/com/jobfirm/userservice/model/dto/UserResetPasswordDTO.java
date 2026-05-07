package com.jobfirm.userservice.model.dto;

import lombok.Data;

/**
 * 找回密码 DTO
 */
@Data
public class UserResetPasswordDTO {
    private String phone;
    private String email;
    private String oldPassword;
    private String newPassword;
}
