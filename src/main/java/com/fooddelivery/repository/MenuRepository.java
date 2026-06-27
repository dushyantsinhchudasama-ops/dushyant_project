package com.fooddelivery.repository;

import com.fooddelivery.model.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {
    List<MenuItem> findAll();
    Optional<MenuItem> findById(int id);
    List<MenuItem> findByCategoryId(int categoryId);
    List<MenuItem> findAvailableItems();
    void save(MenuItem menuItem);
    void update(MenuItem menuItem);
    void deleteById(int id);
    boolean existsByNameAndCategory(String name, int categoryId);
}
