package com.financebuddy.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UserAuthData user;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserAuthData {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String avatarUrl;
        private BigDecimal monthlyBudget;
    }
}