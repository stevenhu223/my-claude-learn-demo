package com.financebuddy.utils;

import com.financebuddy.config.JwtConfig;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtConfig jwtConfig;

    public Long getUserIdFromToken(String token) {
        return jwtConfig.getUserIdFromToken(token);
    }

    public boolean isTokenExpired(String token) {
        return jwtConfig.isTokenExpired(token);
    }

    public Claims getClaimsFromToken(String token) {
        return jwtConfig.getClaimsFromToken(token);
    }
}