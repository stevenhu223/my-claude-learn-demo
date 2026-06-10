package com.financebuddy.config;

import com.financebuddy.common.exception.BusinessException;
import com.financebuddy.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录，请先登录");
        }

        token = token.substring(7);

        if (jwtUtils.isTokenExpired(token)) {
            throw new BusinessException(401, "Token已过期，请重新登录");
        }

        Long userId = jwtUtils.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        return true;
    }
}