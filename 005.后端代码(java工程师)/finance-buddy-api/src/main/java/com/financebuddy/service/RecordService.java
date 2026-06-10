package com.financebuddy.service;

import com.financebuddy.dto.request.RecordCreateRequest;
import com.financebuddy.entity.Record;
import java.util.List;

public interface RecordService {
    Record create(RecordCreateRequest request);
    List<Record> listByUserId(Long userId, Integer year, Integer month);
    void delete(Long id);
}