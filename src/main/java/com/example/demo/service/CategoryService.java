package com.example.demo.service;


import com.example.demo.DTO.CategoryDTO;
import com.example.demo.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category createCategory(CategoryDTO categoryDTO);

    Category updateCategory(Long id, CategoryDTO categoryDTO);

    Optional<Category> getCategoryById(Long id);

    Optional<Category> getCategoryByName(String name);

    List<Category> getAllCategories();

    Page<Category> getAllCategories(Pageable pageable);

    List<Category> getAllParentCategories();

    List<Category> getSubcategoriesByParentId(Long parentId);

    void deleteCategory(Long id);

    boolean hasSubcategories(Long categoryId);

    boolean hasProducts(Long categoryId);
}