package com.financebuddy.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BudgetRequest {
    private String ym;  // 年月，如 "2026-06"
    private BigDecimal budgetAmount;
}