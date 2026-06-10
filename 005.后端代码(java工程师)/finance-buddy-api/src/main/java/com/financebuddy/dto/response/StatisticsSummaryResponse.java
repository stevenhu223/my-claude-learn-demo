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
public class StatisticsSummaryResponse {
    @JsonProperty("total_income")
    private BigDecimal totalIncome;

    @JsonProperty("total_expense")
    private BigDecimal totalExpense;

    private BigDecimal balance;

    @JsonProperty("expense_count")
    private Integer expenseCount;

    @JsonProperty("income_count")
    private Integer incomeCount;

    @JsonProperty("daily_avg_income")
    private BigDecimal dailyAvgIncome;

    @JsonProperty("daily_avg_expense")
    private BigDecimal dailyAvgExpense;

    @JsonProperty("income_change_pct")
    private String incomeChangePct;

    @JsonProperty("expense_change_pct")
    private String expenseChangePct;
}