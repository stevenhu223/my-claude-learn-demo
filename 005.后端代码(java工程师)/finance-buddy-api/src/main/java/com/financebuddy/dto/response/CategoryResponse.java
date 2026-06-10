package com.financebuddy.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private Long userId;
    private Integer type;
    private String name;
    private String icon;
    private Integer sortOrder;
    private Boolean isSystem;
}