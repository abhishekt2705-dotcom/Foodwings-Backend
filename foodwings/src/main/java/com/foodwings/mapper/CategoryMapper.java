package com.foodwings.mapper;

import com.foodwings.dto.response.CategoryResponse;
import com.foodwings.entity.Category;

/**
 * Maps {@link Category} entities to response DTOs.
 */
public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .imagePath(c.getImagePath())
                .active(c.isActive())
                .build();
    }
}
