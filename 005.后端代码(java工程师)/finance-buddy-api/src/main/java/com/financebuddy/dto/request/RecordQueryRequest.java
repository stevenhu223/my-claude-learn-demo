package com.financebuddy.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RecordQueryRequest {
    private Integer type;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer limit = 50;
    private Integer offset = 0;
}