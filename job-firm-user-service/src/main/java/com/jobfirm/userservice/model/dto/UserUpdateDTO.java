package com.jobfirm.userservice.model.dto;

import lombok.Data;

/**
 * 用户资料更新 DTO
 */
@Data
public class UserUpdateDTO {

    private String username;
    private String phone;
    private String email;
}
