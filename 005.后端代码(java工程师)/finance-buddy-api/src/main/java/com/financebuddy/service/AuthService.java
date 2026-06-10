package com.financebuddy.service;

import com.financebuddy.dto.request.LoginRequest;
import com.financebuddy.dto.request.RegisterRequest;
import com.financebuddy.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout();
}