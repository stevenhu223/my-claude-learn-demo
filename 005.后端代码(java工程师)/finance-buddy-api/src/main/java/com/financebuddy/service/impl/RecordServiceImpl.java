package com.financebuddy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.financebuddy.common.exception.BusinessException;
import com.financebuddy.dto.request.RecordCreateRequest;
import com.financebuddy.entity.Record;
import com.financebuddy.repository.RecordRepository;
import com.financebuddy.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;

    @Override
    public Record create(RecordCreateRequest request) {
        Record record = new Record();
        record.setUserId(request.getUserId());
        record.setType(request.getType());
        record.setAmount(request.getAmount());
        record.setCategoryId(request.getCategoryId());
        record.setAccountId(request.getAccountId());
        record.setPaymentMethodId(request.getPaymentMethodId());
        record.setRecordDate(request.getRecordDate());
        record.setRemark(request.getRemark());
        recordRepository.insert(record);
        return record;
    }

    @Override
    public List<Record> listByUserId(Long userId, Integer year, Integer month) {
        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Record::getUserId, userId)
               .isNull(Record::getDeletedAt);

        if (year != null && month != null) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            wrapper.ge(Record::getRecordDate, startDate)
                   .le(Record::getRecordDate, endDate);
        }

        wrapper.orderByDesc(Record::getRecordDate);
        return recordRepository.selectList(wrapper);
    }

    @Override
    public void delete(Long id) {
        Record record = recordRepository.selectById(id);
        if (record == null) {
            throw new BusinessException("记录不存在");
        }

        LambdaUpdateWrapper<Record> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Record::getDeletedAt, LocalDate.now())
               .eq(Record::getId, id);
        recordRepository.update(null, wrapper);
    }
}