package com.financebuddy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.financebuddy.dto.response.*;
import com.financebuddy.entity.Budget;
import com.financebuddy.entity.Category;
import com.financebuddy.entity.Record;
import com.financebuddy.repository.BudgetRepository;
import com.financebuddy.repository.CategoryRepository;
import com.financebuddy.repository.RecordRepository;
import com.financebuddy.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final RecordRepository recordRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    @Override
    public StatisticsSummaryResponse getSummary(Long userId, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

        LocalDate lastMonthDate = startDate.minusMonths(1);
        LocalDate lastStartDate = LocalDate.of(lastMonthDate.getYear(), lastMonthDate.getMonthValue(), 1);
        LocalDate lastEndDate = YearMonth.of(lastMonthDate.getYear(), lastMonthDate.getMonthValue()).atEndOfMonth();

        // 当月数据
        LambdaQueryWrapper<Record> currWrapper = new LambdaQueryWrapper<>();
        currWrapper.eq(Record::getUserId, userId)
                   .ge(Record::getRecordDate, startDate)
                   .le(Record::getRecordDate, endDate)
                   .isNull(Record::getDeletedAt);
        List<Record> currentRecords = recordRepository.selectList(currWrapper);

        // 上月数据
        LambdaQueryWrapper<Record> lastWrapper = new LambdaQueryWrapper<>();
        lastWrapper.eq(Record::getUserId, userId)
                   .ge(Record::getRecordDate, lastStartDate)
                   .le(Record::getRecordDate, lastEndDate)
                   .isNull(Record::getDeletedAt);
        List<Record> lastRecords = recordRepository.selectList(lastWrapper);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        int incomeCount = 0;
        int expenseCount = 0;

        for (Record r : currentRecords) {
            if (r.getType() == 2) {
                totalIncome = totalIncome.add(r.getAmount());
                incomeCount++;
            } else {
                totalExpense = totalExpense.add(r.getAmount());
                expenseCount++;
            }
        }

        BigDecimal lastIncome = BigDecimal.ZERO;
        BigDecimal lastExpense = BigDecimal.ZERO;
        for (Record r : lastRecords) {
            if (r.getType() == 2) {
                lastIncome = lastIncome.add(r.getAmount());
            } else {
                lastExpense = lastExpense.add(r.getAmount());
            }
        }

        BigDecimal balance = totalIncome.subtract(totalExpense);
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        BigDecimal dailyAvgIncome = totalIncome.divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);
        BigDecimal dailyAvgExpense = totalExpense.divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);

        String incomeChangePct = "0.0";
        if (lastIncome.compareTo(BigDecimal.ZERO) > 0) {
            incomeChangePct = totalIncome.subtract(lastIncome)
                    .divide(lastIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP)
                    .toString();
        }
        String expenseChangePct = "0.0";
        if (lastExpense.compareTo(BigDecimal.ZERO) > 0) {
            expenseChangePct = totalExpense.subtract(lastExpense)
                    .divide(lastExpense, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP)
                    .toString();
        }

        return StatisticsSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .incomeCount(incomeCount)
                .expenseCount(expenseCount)
                .dailyAvgIncome(dailyAvgIncome)
                .dailyAvgExpense(dailyAvgExpense)
                .incomeChangePct(incomeChangePct)
                .expenseChangePct(expenseChangePct)
                .build();
    }

    @Override
    public StatisticsTrendResponse getTrend(Long userId, Integer year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Record::getUserId, userId)
               .ge(Record::getRecordDate, startDate)
               .le(Record::getRecordDate, endDate)
               .isNull(Record::getDeletedAt);
        List<Record> records = recordRepository.selectList(wrapper);

        // 按月份聚合
        Map<Integer, List<Record>> byMonth = records.stream()
                .collect(Collectors.groupingBy(r -> r.getRecordDate().getMonthValue()));

        List<StatisticsTrendResponse.MonthlyData> monthlyData = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            BigDecimal income = BigDecimal.ZERO;
            BigDecimal expense = BigDecimal.ZERO;
            List<Record> monthRecords = byMonth.get(m);
            if (monthRecords != null) {
                for (Record r : monthRecords) {
                    if (r.getType() == 2) {
                        income = income.add(r.getAmount());
                    } else {
                        expense = expense.add(r.getAmount());
                    }
                }
            }
            monthlyData.add(StatisticsTrendResponse.MonthlyData.builder()
                    .month(String.format("%02d", m))
                    .income(income)
                    .expense(expense)
                    .build());
        }

        return StatisticsTrendResponse.builder()
                .yearly(String.valueOf(year))
                .monthlyData(monthlyData)
                .build();
    }

    @Override
    public CategoryBreakdownResponse getCategoryBreakdown(Long userId, Integer year, Integer month, Integer type) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Record::getUserId, userId)
               .ge(Record::getRecordDate, startDate)
               .le(Record::getRecordDate, endDate)
               .isNull(Record::getDeletedAt);
        if (type != null) {
            wrapper.eq(Record::getType, type);
        }
        List<Record> records = recordRepository.selectList(wrapper);

        // 按分类聚合
        Map<Long, List<Record>> byCategory = records.stream()
                .collect(Collectors.groupingBy(Record::getCategoryId));

        BigDecimal grandTotal = BigDecimal.ZERO;
        List<CategoryBreakdownResponse.CategoryItem> items = new ArrayList<>();

        for (Map.Entry<Long, List<Record>> entry : byCategory.entrySet()) {
            Long catId = entry.getKey();
            List<Record> catRecords = entry.getValue();

            BigDecimal total = catRecords.stream()
                    .map(Record::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            grandTotal = grandTotal.add(total);

            Category cat = categoryRepository.selectById(catId);
            String name = cat != null ? cat.getName() : "未知";
            String icon = cat != null ? cat.getIcon() : "help";
            Integer catType = cat != null ? cat.getType() : 1;

            String pct = grandTotal.compareTo(BigDecimal.ZERO) > 0
                    ? total.divide(grandTotal, 4, RoundingMode.HALF_UP)
                           .multiply(BigDecimal.valueOf(100))
                           .setScale(1, RoundingMode.HALF_UP)
                           .toString()
                    : "0.0";

            items.add(CategoryBreakdownResponse.CategoryItem.builder()
                    .id(catId)
                    .name(name)
                    .icon(icon)
                    .type(catType)
                    .total(total)
                    .percentage(pct)
                    .count(catRecords.size())
                    .build());
        }

        // 按金额降序
        items.sort((a, b) -> b.getTotal().compareTo(a.getTotal()));

        return CategoryBreakdownResponse.builder()
                .categories(items)
                .total(grandTotal)
                .build();
    }

    @Override
    public TodayStatsResponse getToday(Long userId) {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Record::getUserId, userId)
               .eq(Record::getRecordDate, today)
               .isNull(Record::getDeletedAt);
        List<Record> todayRecords = recordRepository.selectList(wrapper);

        BigDecimal expense = BigDecimal.ZERO;
        BigDecimal income = BigDecimal.ZERO;
        for (Record r : todayRecords) {
            if (r.getType() == 1) {
                expense = expense.add(r.getAmount());
            } else {
                income = income.add(r.getAmount());
            }
        }
        BigDecimal balance = income.subtract(expense);

        // 月度预算
        String ym = String.format("%d-%02d", year, month);
        LambdaQueryWrapper<Budget> bw = new LambdaQueryWrapper<>();
        bw.eq(Budget::getUserId, userId).eq(Budget::getYm, ym);
        Budget budget = budgetRepository.selectOne(bw);
        BigDecimal monthlyBudget = budget != null ? budget.getBudgetAmount() : BigDecimal.ZERO;
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        BigDecimal dailyBudget = monthlyBudget.divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);

        return TodayStatsResponse.builder()
                .date(today.toString())
                .expense(expense)
                .income(income)
                .balance(balance)
                .dailyBudget(dailyBudget)
                .monthlyBudget(monthlyBudget)
                .build();
    }

    @Override
    public BudgetOverviewResponse getBudgetOverview(Long userId, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();
        String ym = String.format("%d-%02d", year, month);

        // 预算
        LambdaQueryWrapper<Budget> bw = new LambdaQueryWrapper<>();
        bw.eq(Budget::getUserId, userId).eq(Budget::getYm, ym);
        Budget budget = budgetRepository.selectOne(bw);
        BigDecimal totalBudget = budget != null ? budget.getBudgetAmount() : BigDecimal.ZERO;

        // 支出
        LambdaQueryWrapper<Record> rw = new LambdaQueryWrapper<>();
        rw.eq(Record::getUserId, userId)
          .eq(Record::getType, 1)
          .ge(Record::getRecordDate, startDate)
          .le(Record::getRecordDate, endDate)
          .isNull(Record::getDeletedAt);
        List<Record> expenses = recordRepository.selectList(rw);
        BigDecimal totalSpent = expenses.stream()
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = totalBudget.subtract(totalSpent);
        String pct = totalBudget.compareTo(BigDecimal.ZERO) > 0
                ? totalSpent.divide(totalBudget, 4, RoundingMode.HALF_UP)
                           .multiply(BigDecimal.valueOf(100))
                           .setScale(1, RoundingMode.HALF_UP)
                           .toString()
                : "0.0";

        return BudgetOverviewResponse.builder()
                .totalBudget(totalBudget)
                .totalSpent(totalSpent)
                .remaining(remaining)
                .percentage(pct)
                .build();
    }
}