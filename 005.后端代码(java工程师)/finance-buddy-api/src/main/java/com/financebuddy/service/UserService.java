package com.financebuddy.service;

import com.financebuddy.dto.request.UserUpdateRequest;
import com.financebuddy.dto.response.UserInfoResponse;

public interface UserService {
    UserInfoResponse getUserInfo(Long userId);
    UserInfoResponse updateUser(Long userId, UserUpdateRequest request);
}