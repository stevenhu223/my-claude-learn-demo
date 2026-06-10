package com.financebuddy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("records")
public class Record {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer type;  // 1: 支出, 2: 收入

    private BigDecimal amount;

    private Long categoryId;

    private Long accountId;

    private Long paymentMethodId;

    private LocalDate recordDate;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}