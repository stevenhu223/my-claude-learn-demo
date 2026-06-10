package com.financebuddy.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserUpdateRequest {
    private String nickname;
    private String email;
    private String avatarUrl;
    private BigDecimal monthlyBudget;
}