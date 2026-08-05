package com.foodwings.controller;

import com.foodwings.dto.request.CategoryRequest;
import com.foodwings.dto.response.CategoryResponse;
import com.foodwings.response.ApiResponse;
import com.foodwings.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Categories fetched successfully",
                        categoryService.getAll()
                )
        );
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category fetched successfully",
                        categoryService.getById(id)
                )
        );
    }

    @PostMapping("/category")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @Valid @RequestBody CategoryRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category created successfully",
                        categoryService.create(request)
                )
        );
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category updated successfully",
                        categoryService.update(id, request)
                )
        );
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {
        categoryService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category deleted successfully",
                        null
                )
        );
    }

    @PostMapping("/category/{id}/image")
    public ResponseEntity<ApiResponse<CategoryResponse>> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Image uploaded successfully",
                        categoryService.uploadImage(id, file)
                )
        );
    }
}