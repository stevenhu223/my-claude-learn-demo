package com.financebuddy.controller;

import com.financebuddy.common.result.Result;
import com.financebuddy.dto.response.*;
import com.financebuddy.service.StatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/summary")
    public Result<StatisticsSummaryResponse> summary(
            HttpServletRequest req,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Long userId = (Long) req.getAttribute("userId");
        return Result.success(statisticsService.getSummary(userId, year, month));
    }

    @GetMapping("/trend")
    public Result<StatisticsTrendResponse> trend(
            HttpServletRequest req,
            @RequestParam Integer year) {
        Long userId = (Long) req.getAttribute("userId");
        return Result.success(statisticsService.getTrend(userId, year));
    }

    @GetMapping("/category-breakdown")
    public Result<CategoryBreakdownResponse> categoryBreakdown(
            HttpServletRequest req,
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(required = false) Integer type) {
        Long userId = (Long) req.getAttribute("userId");
        return Result.success(statisticsService.getCategoryBreakdown(userId, year, month, type));
    }

    @GetMapping("/today")
    public Result<TodayStatsResponse> today(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return Result.success(statisticsService.getToday(userId));
    }

    @GetMapping("/budget-overview")
    public Result<BudgetOverviewResponse> budgetOverview(
            HttpServletRequest req,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Long userId = (Long) req.getAttribute("userId");
        return Result.success(statisticsService.getBudgetOverview(userId, year, month));
    }
}