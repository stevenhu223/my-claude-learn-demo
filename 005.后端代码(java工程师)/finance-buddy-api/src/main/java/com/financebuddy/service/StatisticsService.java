package com.financebuddy.service;

import com.financebuddy.dto.response.*;

public interface StatisticsService {
    StatisticsSummaryResponse getSummary(Long userId, Integer year, Integer month);
    StatisticsTrendResponse getTrend(Long userId, Integer year);
    CategoryBreakdownResponse getCategoryBreakdown(Long userId, Integer year, Integer month, Integer type);
    TodayStatsResponse getToday(Long userId);
    BudgetOverviewResponse getBudgetOverview(Long userId, Integer year, Integer month);
}