package com.financebuddy.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.financebuddy.entity.PaymentMethod;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMethodRepository extends BaseMapper<PaymentMethod> {
}