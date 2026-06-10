package com.financebuddy.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatarUrl;
    private BigDecimal monthlyBudget;
    private LocalDateTime createdAt;
}