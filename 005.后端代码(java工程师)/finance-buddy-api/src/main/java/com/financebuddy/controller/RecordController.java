package com.financebuddy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.financebuddy.common.result.Result;
import com.financebuddy.dto.request.RecordCreateRequest;
import com.financebuddy.dto.response.RecordResponse;
import com.financebuddy.entity.Category;
import com.financebuddy.entity.Record;
import com.financebuddy.repository.CategoryRepository;
import com.financebuddy.repository.RecordRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordRepository recordRepository;
    private final CategoryRepository categoryRepository;

    @PostMapping
    public Result<RecordResponse> create(
            @Valid @RequestBody RecordCreateRequest request,
            HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        request.setUserId(userId);

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

        return Result.created("记录创建成功", toResponse(record));
    }

    @GetMapping
    public Result<List<RecordResponse>> list(
            HttpServletRequest req,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "50") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {

        Long userId = (Long) req.getAttribute("userId");

        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Record::getUserId, userId)
               .isNull(Record::getDeletedAt)
               .orderByDesc(Record::getRecordDate)
               .last("LIMIT 50");

        if (type != null) {
            wrapper.eq(Record::getType, type);
        }
        if (startDate != null) {
            wrapper.ge(Record::getRecordDate, java.time.LocalDate.parse(startDate));
        }
        if (endDate != null) {
            wrapper.le(Record::getRecordDate, java.time.LocalDate.parse(endDate));
        }

        List<Record> records = recordRepository.selectList(wrapper);

        // 获取所有分类
        Map<Long, Category> categoryMap = categoryRepository.selectList(null).stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        List<RecordResponse> responses = records.stream()
                .map(r -> {
                    RecordResponse.RecordResponseBuilder builder = RecordResponse.builder()
                            .id(r.getId())
                            .userId(r.getUserId())
                            .type(r.getType())
                            .amount(r.getAmount())
                            .categoryId(r.getCategoryId())
                            .accountId(r.getAccountId())
                            .paymentMethodId(r.getPaymentMethodId())
                            .recordDate(r.getRecordDate())
                            .remark(r.getRemark())
                            .createdAt(r.getCreatedAt())
                            .updatedAt(r.getUpdatedAt());

                    if (r.getCategoryId() != null && categoryMap.containsKey(r.getCategoryId())) {
                        Category cat = categoryMap.get(r.getCategoryId());
                        builder.categoryName(cat.getName())
                              .categoryIcon(cat.getIcon());
                    }
                    return builder.build();
                })
                .collect(Collectors.toList());

        return Result.success(responses);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Record record = recordRepository.selectById(id);
        if (record == null) {
            return Result.error(404, "记录不存在");
        }
        record.setDeletedAt(java.time.LocalDateTime.now());
        recordRepository.updateById(record);
        return Result.success();
    }

    private RecordResponse toResponse(Record r) {
        RecordResponse.RecordResponseBuilder builder = RecordResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .type(r.getType())
                .amount(r.getAmount())
                .categoryId(r.getCategoryId())
                .accountId(r.getAccountId())
                .paymentMethodId(r.getPaymentMethodId())
                .recordDate(r.getRecordDate())
                .remark(r.getRemark())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt());

        if (r.getCategoryId() != null) {
            Category cat = categoryRepository.selectById(r.getCategoryId());
            if (cat != null) {
                builder.categoryName(cat.getName())
                      .categoryIcon(cat.getIcon());
            }
        }
        return builder.build();
    }
}