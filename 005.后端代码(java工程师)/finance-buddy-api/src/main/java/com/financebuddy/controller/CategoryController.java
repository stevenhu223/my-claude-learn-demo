package com.financebuddy.controller;

import com.financebuddy.common.result.Result;
import com.financebuddy.dto.response.CategoryResponse;
import com.financebuddy.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<List<CategoryResponse>> list(
            HttpServletRequest req,
            @RequestParam(required = false) Integer type) {
        Long userId = (Long) req.getAttribute("userId");
        List<CategoryResponse> categories = categoryService.listByUserIdAndType(userId, type);
        return Result.success(categories);
    }
}