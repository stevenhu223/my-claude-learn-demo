package com.financebuddy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("budgets")
public class Budget {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String ym;  // year-month, e.g. "2026-06"

    private BigDecimal budgetAmount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}