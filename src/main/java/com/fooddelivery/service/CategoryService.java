package com.fooddelivery.service;

import com.fooddelivery.exception.CategoryNotFoundException;
import com.fooddelivery.exception.AlreadyExistsException;
import com.fooddelivery.model.Category;
import com.fooddelivery.repository.CategoryRepository;

import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category addCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new AlreadyExistsException("Category already exists: " + name);
        }
        int id = getNextCategoryId();
        Category category = new Category(id, name);
        categoryRepository.save(category);
        return category;
    }

    public Category updateCategory(int categoryId, String name) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + categoryId));

        if (!category.getName().equalsIgnoreCase(name) && categoryRepository.existsByName(name)) {
            throw new AlreadyExistsException("Category already exists: " + name);
        }

        category.setName(name);
        categoryRepository.update(category);
        return category;
    }

    public void deleteCategory(int categoryId) {
        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new CategoryNotFoundException("Category not found: " + categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + categoryId));
    }

    private int getNextCategoryId() {
        return categoryRepository.findAll().stream()
                .mapToInt(Category::getId)
                .max()
                .orElse(0) + 1;
    }
}