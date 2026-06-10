package com.financebuddy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.financebuddy.common.exception.BusinessException;
import com.financebuddy.dto.request.UserUpdateRequest;
import com.financebuddy.dto.response.UserInfoResponse;
import com.financebuddy.entity.User;
import com.financebuddy.repository.UserRepository;
import com.financebuddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.selectById(userId);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException("用户不存在");
        }
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .monthlyBudget(user.getMonthlyBudget())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public UserInfoResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.selectById(userId);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException("用户不存在");
        }

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getMonthlyBudget() != null) {
            user.setMonthlyBudget(request.getMonthlyBudget());
        }

        userRepository.updateById(user);
        return getUserInfo(userId);
    }
}