package com.financebuddy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsTrendResponse {
    private String yearly;

    @JsonProperty("monthly_data")
    private List<MonthlyData> monthlyData;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyData {
        private String month;
        private BigDecimal income;
        private BigDecimal expense;
    }
}