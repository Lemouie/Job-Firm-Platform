package com.jobfirm.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jobfirm.api.auth.AuthClient;
import com.jobfirm.userservice.enums.UserRoleEnum;
import com.jobfirm.userservice.enums.UserStatusEnum;
import com.jobfirm.common.exception.BusinessException;
import com.jobfirm.common.result.ErrorCode;
import com.jobfirm.userservice.mapper.UserMapper;
import com.jobfirm.userservice.model.dto.*;
import com.jobfirm.userservice.model.entity.User;
import com.jobfirm.userservice.model.vo.UserVO;
import com.jobfirm.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

/**
 * 用户服务实现类
 * 实现用户注册、登录、资料修改、禁用等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final AuthClient authClient;

    /** 密码加密（BCrypt） */
    private String encrypt(String raw) {
        return new BCryptPasswordEncoder().encode(raw);
    }

    @Override
    public Long register(UserRegisterDTO dto) {
        // 手机号或邮箱不能重复
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getPhone() != null, User::getPhone, dto.getPhone())
                .or()
                .eq(dto.getEmail() != null, User::getEmail, dto.getEmail());

        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTER);
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(encrypt(dto.getPassword()));
        user.setRole(String.valueOf(UserRoleEnum.CUSTOMER));
        if(UserRoleEnum.CEO.equals(dto.getRole()))
            user.setRole(String.valueOf(UserRoleEnum.CEO));
        user.setStatus("ACTIVE");

        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public String login(UserLoginDTO dto) {
        User user = null;

        if (StringUtils.isNotBlank(dto.getPhone())) {
            user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone())
            );
        }

        if (user == null && StringUtils.isNotBlank(dto.getEmail())) {
            user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getEmail, dto.getEmail())
            );
        }


        if (user == null || !new BCryptPasswordEncoder().matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_NAME_OR_PASSWORD_ERROR);
        }

        if (UserStatusEnum.DISABLED.equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // ============================
        // 调用 auth-service 生成 Token
        // ============================
        String token = authClient.generateToken(user.getId(), user.getRole()).getData();

        return token;
    }

    @Override
    public boolean resetPassword(UserResetPasswordDTO dto) {
        User user = null;

        if (StringUtils.isNotBlank(dto.getPhone())) {
            user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone())
            );
        }

        if (user == null && StringUtils.isNotBlank(dto.getEmail())) {
            user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getEmail, dto.getEmail())
            );
        }

        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_NAME_OR_PASSWORD_ERROR);
        }

        user.setPassword(encrypt(dto.getNewPassword()));
        return userMapper.updateById(user) > 0;
    }


    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public boolean updateUser(Long userId, UserUpdateDTO dto) {
        User user = userMapper.selectById(userId);
        BeanUtils.copyProperties(dto, user);
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean updateAvatar(Long userId, UserAvatarDTO dto) {
        User user = userMapper.selectById(userId);
        BeanUtils.copyProperties(dto, user);
        return userMapper.updateById(user) > 0;
    }

    @Override
    public List<UserVO> listUsers() {
        List<User> list = userMapper.selectList(null);

        return list.stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).toList();
    }

    @Override
    public boolean disableUser(Long userId) {
        User user = userMapper.selectById(userId);
        user.setStatus("DISABLED");
        return userMapper.updateById(user) > 0;
    }

    @Override
    public Long countUsers() {
        return userMapper.selectCount(null);
    }
}
