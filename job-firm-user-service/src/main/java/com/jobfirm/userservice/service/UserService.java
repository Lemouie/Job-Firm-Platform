package com.jobfirm.userservice.service;

import com.jobfirm.userservice.model.dto.*;
import com.jobfirm.userservice.model.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 * 定义用户模块的核心业务逻辑
 */
public interface UserService {
    /** ------------------用户注册与登录------------------ */
    /** 用户注册 */
    Long register(UserRegisterDTO dto);

    /** 用户登录（返回用户ID） */
    String login(UserLoginDTO dto);

    /** 找回密码 */
    boolean resetPassword(UserResetPasswordDTO dto);

    /** ------------------用户资料管理------------------ */
    /** 获取用户信息 */
    UserVO getUserInfo(Long userId);

    /** 更新用户用户名、手机号码、邮箱 */
    boolean updateUser(Long userId, UserUpdateDTO dto);

    /** 上传头像 */
    boolean updateAvatar(Long userId, UserAvatarDTO dto);

    /** ------------------用户订单管理------------------ */

    /** ------------------用户管理（管理员）------------------ */
    /**
     * 管理员：查看所有用户列表
     */
    List<UserVO> listUsers();

    /** 禁用用户（管理员） */
    boolean disableUser(Long userId);

    /** 统计用户数量 */
    Long countUsers();
}
