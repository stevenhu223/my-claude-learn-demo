package com.financebuddy.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.financebuddy.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountRepository extends BaseMapper<Account> {
}