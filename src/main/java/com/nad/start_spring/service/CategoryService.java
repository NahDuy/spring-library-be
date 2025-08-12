package com.nad.start_spring.service;

import com.nad.start_spring.dto.request.CategoryRequest;
import com.nad.start_spring.dto.request.UserCreateRequest;
import com.nad.start_spring.dto.request.UserUpdateRequest;
import com.nad.start_spring.dto.response.CategoryResponse;
import com.nad.start_spring.dto.response.UserResponse;
import com.nad.start_spring.entity.Category;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.exception.AppException;
import com.nad.start_spring.exception.ErrorCode;
import com.nad.start_spring.mapper.CategoryMapper;
import com.nad.start_spring.mapper.UserMapper;
import com.nad.start_spring.repository.CategoryRepository;
import com.nad.start_spring.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public CategoryResponse createCategory(CategoryRequest category) {
        if(categoryRepository.existsByName(category.getName()))
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        Category u = categoryMapper.toCategory(category);
        return categoryMapper.toCategoryResponse(categoryRepository.save(u));
    }
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryResponse).toList();

    }
    public CategoryResponse getCategory(String id){
        return categoryMapper.toCategoryResponse(categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)));
    }


    public CategoryResponse updateCategory(CategoryRequest categoryRequest, String id) {
        Category u = categoryRepository.findById(id).orElseThrow(()
                -> new RuntimeException("No find category"));
        u.setName(categoryRequest.getName());
        u.setDescription(categoryRequest.getDescription());
        return categoryMapper.toCategoryResponse(categoryRepository.save(u));
    }
    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }
}
