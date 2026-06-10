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
public class CategoryBreakdownResponse {
    private List<CategoryItem> categories;
    private BigDecimal total;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryItem {
        private Long id;
        private String name;
        private String icon;
        private Integer type;
        private BigDecimal total;
        private String percentage;
        private Integer count;
    }
}