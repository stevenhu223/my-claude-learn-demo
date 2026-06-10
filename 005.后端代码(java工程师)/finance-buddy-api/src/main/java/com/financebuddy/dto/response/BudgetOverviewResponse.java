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
public class BudgetOverviewResponse {
    @JsonProperty("total_budget")
    private BigDecimal totalBudget;

    @JsonProperty("total_spent")
    private BigDecimal totalSpent;
    private BigDecimal remaining;
    private String percentage;
}