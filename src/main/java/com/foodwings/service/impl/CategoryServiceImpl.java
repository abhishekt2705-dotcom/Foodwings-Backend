package com.foodwings.service.impl;

import com.foodwings.dto.request.CategoryRequest;
import com.foodwings.dto.response.CategoryResponse;
import com.foodwings.entity.Category;
import com.foodwings.exception.DuplicateResourceException;
import com.foodwings.exception.ResourceNotFoundException;
import com.foodwings.mapper.CategoryMapper;
import com.foodwings.repository.CategoryRepository;
import com.foodwings.service.CategoryService;
import com.foodwings.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, FileStorageService fileStorageService) {
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Category already exists with name: " + request.getName());
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .build();
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findCategory(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        Category category = findCategory(id);
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return CategoryMapper.toResponse(findCategory(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(CategoryMapper::toResponse).toList();
    }

    @Override
    public CategoryResponse uploadImage(Long id, MultipartFile file) {
        Category category = findCategory(id);
        category.setImagePath(fileStorageService.store(file, "categories"));
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }
}
