package com.financebuddy.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.financebuddy.entity.Record;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecordRepository extends BaseMapper<Record> {
}