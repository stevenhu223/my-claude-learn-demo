package com.financebuddy.controller;

import com.financebuddy.common.result.Result;
import com.financebuddy.dto.request.LoginRequest;
import com.financebuddy.dto.request.RegisterRequest;
import com.financebuddy.dto.response.AuthResponse;
import com.financebuddy.dto.response.UserInfoResponse;
import com.financebuddy.service.AuthService;
import com.financebuddy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.created("注册成功", authService.register(request));
    }

    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/me")
    public Result<UserInfoResponse> me(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return Result.success(userService.getUserInfo(userId));
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("OK");
    }
}