package com.nad.start_spring.controller;


import com.nad.start_spring.dto.request.CategoryRequest;
import com.nad.start_spring.dto.response.ApiResponse;
import com.nad.start_spring.dto.response.CategoryResponse;
import com.nad.start_spring.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;
    @PostMapping
    ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .status(categoryService.createCategory(request))
                .build();
    }
    @GetMapping
    ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .status(categoryService.getAllCategories())
                .build();
    }

    @GetMapping("/{categoryId}")
    ApiResponse<CategoryResponse> getCategory(@PathVariable("categoryId") String categoryId) {
        return ApiResponse.<CategoryResponse>builder()
                .status(categoryService.getCategory(categoryId))
                .build();
    }

    @PutMapping("/{categoryId}")
    ApiResponse<CategoryResponse> updateCategory(@PathVariable String categoryId,
                                                 @RequestBody CategoryRequest categoryRequest) {
        return ApiResponse.<CategoryResponse>builder()
                .status(categoryService.updateCategory(categoryRequest, categoryId))
                .build();
    }

    @DeleteMapping("/{categoryId}")
    ApiResponse<String> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<String>builder()
                .status("Delete complete")
                .build();
    }
}
