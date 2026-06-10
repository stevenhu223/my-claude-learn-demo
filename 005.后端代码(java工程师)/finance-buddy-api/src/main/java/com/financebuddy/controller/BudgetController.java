package com.financebuddy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.financebuddy.common.result.Result;
import com.financebuddy.dto.request.BudgetRequest;
import com.financebuddy.entity.Budget;
import com.financebuddy.repository.BudgetRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetRepository budgetRepository;

    @GetMapping
    public Result<Map<String, Object>> getBudget(HttpServletRequest req, @RequestParam String ym) {
        Long userId = (Long) req.getAttribute("userId");
        LambdaQueryWrapper<Budget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Budget::getUserId, userId).eq(Budget::getYm, ym);
        Budget budget = budgetRepository.selectOne(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("ym", ym);
        result.put("budgetAmount", budget != null ? budget.getBudgetAmount() : null);
        result.put("id", budget != null ? budget.getId() : null);

        return Result.success(result);
    }

    @PostMapping
    public Result<Budget> saveBudget(@Valid @RequestBody BudgetRequest request, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");

        // 查询是否已存在该月预算
        LambdaQueryWrapper<Budget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Budget::getUserId, userId).eq(Budget::getYm, request.getYm());
        Budget existingBudget = budgetRepository.selectOne(wrapper);

        if (existingBudget != null) {
            // 更新
            existingBudget.setBudgetAmount(request.getBudgetAmount());
            budgetRepository.updateById(existingBudget);
            return Result.success(existingBudget);
        } else {
            // 新增
            Budget budget = new Budget();
            budget.setUserId(userId);
            budget.setYm(request.getYm());
            budget.setBudgetAmount(request.getBudgetAmount());
            budgetRepository.insert(budget);
            return Result.success(budget);
        }
    }
}