package com.financebuddy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.financebuddy.common.exception.BusinessException;
import com.financebuddy.config.JwtConfig;
import com.financebuddy.dto.request.LoginRequest;
import com.financebuddy.dto.request.RegisterRequest;
import com.financebuddy.dto.response.AuthResponse;
import com.financebuddy.entity.User;
import com.financebuddy.repository.UserRepository;
import com.financebuddy.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername())
               .isNull(User::getDeletedAt);
        if (userRepository.selectCount(wrapper) > 0) {
            throw new BusinessException(409, "用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        userRepository.insert(user);

        // 生成 Token
        String token = jwtConfig.generateToken(user.getId(), user.getUsername());
        return buildAuthResponse(token, user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername())
               .isNull(User::getDeletedAt);
        User user = userRepository.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 使用 BCrypt 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        String token = jwtConfig.generateToken(user.getId(), user.getUsername());
        return buildAuthResponse(token, user);
    }

    @Override
    public void logout() {
        // JWT 无状态，logout 由前端处理 Token 清除
    }

    private AuthResponse buildAuthResponse(String token, User user) {
        AuthResponse.UserAuthData userResponse = AuthResponse.UserAuthData.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .monthlyBudget(user.getMonthlyBudget())
                .build();

        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }
}