package com.jobfirm.api.auth;

import com.jobfirm.common.auth.vo.TokenValidateVO;
import com.jobfirm.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// auth-service 的 Feign RPC 接口
@FeignClient(name = "auth-service")
public interface AuthClient {

    @PostMapping("/auth/generate")
    Result<String> generateToken(@RequestParam("userId") Long userId,
                                 @RequestParam("role") String role);

    @PostMapping("/auth/validate")
    Result<TokenValidateVO> validateToken(@RequestParam("token") String token);
}
