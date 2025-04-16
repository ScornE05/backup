package com.example.demo.Controller;

import com.example.demo.DTO.ApiResponse;
import com.example.demo.DTO.CategoryDTO;
import com.example.demo.entity.Category;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, ModelMapper modelMapper) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(modelMapper.map(category, CategoryDTO.class), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);

        if (category.getParent() != null) {
            categoryDTO.setParentId(category.getParent().getId());
            categoryDTO.setParentName(category.getParent().getName());
        }

        return ResponseEntity.ok(categoryDTO);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> {
                    CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);
                    if (category.getParent() != null) {
                        dto.setParentId(category.getParent().getId());
                        dto.setParentName(category.getParent().getName());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(categoryDTOs);
    }

    @GetMapping("/pageable")
    public ResponseEntity<Page<CategoryDTO>> getAllCategoriesPageable(Pageable pageable) {
        Page<Category> categories = categoryService.getAllCategories(pageable);
        Page<CategoryDTO> categoryDTOs = categories.map(category -> {
            CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);
            if (category.getParent() != null) {
                dto.setParentId(category.getParent().getId());
                dto.setParentName(category.getParent().getName());
            }
            return dto;
        });

        return ResponseEntity.ok(categoryDTOs);
    }

    @GetMapping("/parent")
    public ResponseEntity<List<CategoryDTO>> getAllParentCategories() {
        List<Category> categories = categoryService.getAllParentCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(categoryDTOs);
    }

    @GetMapping("/subcategories/{parentId}")
    public ResponseEntity<List<CategoryDTO>> getSubcategoriesByParentId(@PathVariable Long parentId) {
        List<Category> subcategories = categoryService.getSubcategoriesByParentId(parentId);
        List<CategoryDTO> subcategoryDTOs = subcategories.stream()
                .map(subcategory -> {
                    CategoryDTO dto = modelMapper.map(subcategory, CategoryDTO.class);
                    dto.setParentId(parentId);
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(subcategoryDTOs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        Category updatedCategory = categoryService.updateCategory(id, categoryDTO);
        CategoryDTO updatedCategoryDTO = modelMapper.map(updatedCategory, CategoryDTO.class);

        if (updatedCategory.getParent() != null) {
            updatedCategoryDTO.setParentId(updatedCategory.getParent().getId());
            updatedCategoryDTO.setParentName(updatedCategory.getParent().getName());
        }

        return ResponseEntity.ok(updatedCategoryDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(new ApiResponse(true, "Category deleted successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}/has-subcategories")
    public ResponseEntity<Boolean> hasSubcategories(@PathVariable Long id) {
        boolean hasSubcategories = categoryService.hasSubcategories(id);
        return ResponseEntity.ok(hasSubcategories);
    }

    @GetMapping("/{id}/has-products")
    public ResponseEntity<Boolean> hasProducts(@PathVariable Long id) {
        boolean hasProducts = categoryService.hasProducts(id);
        return ResponseEntity.ok(hasProducts);
    }
}
