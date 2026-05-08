package com.jobfirm.auth.controller;

import com.jobfirm.common.auth.vo.TokenValidateVO;
import com.jobfirm.auth.service.AuthService;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.common.result.Result;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/generate")
    public Result<String> generate(@RequestParam("userId") Long userId,
                                   @RequestParam("role") String role) {
        return Result.success(authService.generateToken(userId, role));
    }

    @PostMapping("/validate")
    public Result<TokenValidateVO> validate(
            @RequestHeader("Authorization") String authHeader) {

        // 1️⃣ 校验 Header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.fail(ErrorCode.FAIL.getCode(), "Missing or invalid Authorization header");
        }

        // 2️⃣ 提取 token
        String token = authHeader.substring(7);

        // 3️⃣ 校验 token
        TokenValidateVO vo = authService.validateToken(token);

        if (!vo.getValid()) {
            return Result.fail(ErrorCode.UNAUTHORIZED.getCode(), "fail");
        }

        return Result.success(vo);
    }


    @PostMapping("/parse")
    public Result<Claims> parse(@RequestParam("token") String token) {
        return Result.success(authService.parseToken(token));
    }

}
