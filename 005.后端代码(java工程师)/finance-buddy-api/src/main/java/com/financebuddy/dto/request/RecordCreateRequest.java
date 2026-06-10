package com.financebuddy.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecordCreateRequest {
    private Long userId;

    @NotNull(message = "类型不能为空")
    private Integer type;

    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须为正数")
    private BigDecimal amount;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private Long accountId;

    private Long paymentMethodId;

    @NotNull(message = "日期不能为空")
    private LocalDate recordDate;

    private String remark;
}