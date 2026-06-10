package com.financebuddy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodayStatsResponse {
    private String date;

    private BigDecimal expense;
    private BigDecimal income;
    private BigDecimal balance;

    @JsonProperty("daily_budget")
    private BigDecimal dailyBudget;

    @JsonProperty("monthly_budget")
    private BigDecimal monthlyBudget;
}