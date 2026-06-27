package com.fooddelivery.repository;

import com.fooddelivery.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findAll();
    Optional<Category> findById(int id);
    Optional<Category> findByName(String name);
    void save(Category category);
    void update(Category category);
    void deleteById(int id);
    boolean existsByName(String name);
}
