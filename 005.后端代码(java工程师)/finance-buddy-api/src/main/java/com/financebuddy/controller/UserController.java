package com.financebuddy.controller;

import com.financebuddy.common.result.Result;
import com.financebuddy.dto.request.UserUpdateRequest;
import com.financebuddy.dto.response.UserInfoResponse;
import com.financebuddy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<UserInfoResponse> getCurrentUser(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return Result.success(userService.getUserInfo(userId));
    }

    @PutMapping("/me")
    public Result<UserInfoResponse> updateCurrentUser(
            HttpServletRequest req,
            @Valid @RequestBody UserUpdateRequest request) {
        Long userId = (Long) req.getAttribute("userId");
        return Result.success(userService.updateUser(userId, request));
    }
}