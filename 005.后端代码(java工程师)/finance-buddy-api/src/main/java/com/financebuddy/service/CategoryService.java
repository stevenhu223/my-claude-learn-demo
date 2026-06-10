package com.financebuddy.service;

import com.financebuddy.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> listByUserIdAndType(Long userId, Integer type);
    List<CategoryResponse> listSystemCategories(Integer type);
}