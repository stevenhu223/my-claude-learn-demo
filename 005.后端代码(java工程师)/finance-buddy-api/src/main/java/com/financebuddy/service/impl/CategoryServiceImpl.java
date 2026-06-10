package com.financebuddy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.financebuddy.dto.response.CategoryResponse;
import com.financebuddy.entity.Category;
import com.financebuddy.repository.CategoryRepository;
import com.financebuddy.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> listByUserIdAndType(Long userId, Integer type) {
        // 返回系统分类(user_id=0) + 当前用户的自定义分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(Category::getDeletedAt)
               .and(w -> w.eq(Category::getUserId, 0L).or().eq(Category::getUserId, userId));
        if (type != null) {
            wrapper.eq(Category::getType, type);
        }
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> categories = categoryRepository.selectList(wrapper);
        return categories.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> listSystemCategories(Integer type) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(Category::getDeletedAt)
               .and(w -> w.eq(Category::getUserId, 0L).or().eq(Category::getIsSystem, true));
        if (type != null) {
            wrapper.eq(Category::getType, type);
        }
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> categories = categoryRepository.selectList(wrapper);
        return categories.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private CategoryResponse toResponse(Category cat) {
        return CategoryResponse.builder()
                .id(cat.getId())
                .userId(cat.getUserId())
                .type(cat.getType())
                .name(cat.getName())
                .icon(cat.getIcon())
                .sortOrder(cat.getSortOrder())
                .isSystem(cat.getIsSystem())
                .build();
    }
}