package com.financebuddy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordResponse {
    private Long id;
    private Long userId;
    private Integer type;
    private BigDecimal amount;
    private Long categoryId;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("category_icon")
    private String categoryIcon;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("account_name")
    private String accountName;

    @JsonProperty("payment_method_id")
    private Long paymentMethodId;

    @JsonProperty("payment_method_name")
    private String paymentMethodName;

    @JsonProperty("record_date")
    private LocalDate recordDate;
    private String remark;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}