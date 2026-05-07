package com.jobfirm.common.auth.vo;

import lombok.Data;

@Data
public class TokenValidateVO {
    private Boolean valid;
    private String userId;
    private String role;
    private String newToken;
}
