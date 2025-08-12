package com.nad.start_spring.mapper;

import com.nad.start_spring.dto.request.CategoryRequest;
import com.nad.start_spring.dto.response.CategoryResponse;
import com.nad.start_spring.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory (CategoryRequest categoryRequest);
    CategoryResponse toCategoryResponse (Category category);
}
