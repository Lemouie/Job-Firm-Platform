package com.jobfirm.userservice.controller;

import com.jobfirm.common.result.Result;
import com.jobfirm.userservice.model.dto.*;
import com.jobfirm.userservice.model.vo.UserVO;
import com.jobfirm.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * 提供用户注册、登录、资料管理、管理员操作等接口
 * 所有请求均通过 Gateway 转发
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Long> register(@RequestBody UserRegisterDTO dto) {
        return Result.success(userService.register(dto));
    }

    /**
     * 用户登录（返回用户ID，由网关生成 JWT）
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserLoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/reset-password")
    public Result<Boolean> resetPassword(@RequestBody UserResetPasswordDTO dto) {
        return Result.success(userService.resetPassword(dto));
    }


    /**
     * 获取当前用户信息
     * userId 由 Gateway 从 JWT 中解析后放入请求头：X-User-Id
     */
    @GetMapping("/me")
    public Result<UserVO> getUserInfo(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(userService.getUserInfo(userId));
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/update")
    public Result<Boolean> updateUser(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UserUpdateDTO dto) {
        return Result.success(userService.updateUser(userId, dto));
    }

    @PutMapping("/avatar")
    public Result<Boolean> updateAvatar(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UserAvatarDTO dto) {
        return Result.success(userService.updateAvatar(userId, dto));
    }

    /**
     * 管理员：查看所有用户列表
     */
    @GetMapping("/list")
    public Result<List<UserVO>> listUsers() {
        return Result.success(userService.listUsers());
    }

    /**
     * 禁用用户（管理员）
     */
    @PostMapping("/{id}/disable")
    public Result<Boolean> disableUser(@PathVariable Long id) {
        return Result.success(userService.disableUser(id));
    }

    /**
     * 统计用户数量（管理员）
     */
    @GetMapping("/count")
    public Result<Long> countUsers() {
        return Result.success(userService.countUsers());
    }
}
