package com.financebuddy.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.financebuddy.entity.Budget;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BudgetRepository extends BaseMapper<Budget> {
}