package com.jobfirm.auth.service;

import com.jobfirm.common.auth.vo.TokenValidateVO;
import io.jsonwebtoken.Claims;

public interface AuthService {

    String generateToken(Long userId, String role);

    TokenValidateVO validateToken(String token);

    Claims parseToken(String token);
}
