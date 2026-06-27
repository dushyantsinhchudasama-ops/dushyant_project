package com.fooddelivery.repository;

import com.fooddelivery.exception.DataAccessException;
import com.fooddelivery.model.Category;
import com.fooddelivery.utility.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileCategoryRepository implements CategoryRepository {
    private static final String DEFAULT_CATEGORIES_FILE = "data/categories.txt";
    private static final String DELIMITER = "\u001F";
    private final List<Category> categories = new ArrayList<>();
    private final String categoriesFile;

    public FileCategoryRepository() {
        this(DEFAULT_CATEGORIES_FILE);
    }

    public FileCategoryRepository(String categoriesFile) {
        this.categoriesFile = categoriesFile;
        loadCategories();
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(categories);
    }

    @Override
    public Optional<Category> findById(int id) {
        return categories.stream()
                .filter(category -> category.getId() == id)
                .findFirst();
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categories.stream()
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public void save(Category category) {
        if (existsByName(category.getName())) {
            throw new DataAccessException("Category already exists: " + category.getName());
        }
        categories.add(category);
        flush();
    }

    @Override
    public void update(Category category) {
        int index = findIndexById(category.getId());
        if (index == -1) {
            throw new DataAccessException("Category not found: " + category.getId());
        }
        categories.set(index, category);
        flush();
    }

    @Override
    public void deleteById(int id) {
        int index = findIndexById(id);
        if (index == -1) {
            throw new DataAccessException("Category not found: " + id);
        }
        categories.remove(index);
        flush();
    }

    @Override
    public boolean existsByName(String name) {
        return categories.stream()
                .anyMatch(category -> category.getName().equalsIgnoreCase(name));
    }

    private void loadCategories() {
        List<String> lines = FileUtil.readAllLines(categoriesFile);
        categories.clear();
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split(DELIMITER, -1);
            if (parts.length != 2) {
                throw new DataAccessException("Invalid category record: " + line);
            }
            int id;
            try {
                id = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                throw new DataAccessException("Invalid category id format: " + line, e);
            }
            categories.add(new Category(id, parts[1]));
        }
    }

    private int findIndexById(int id) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private void flush() {
        List<String> lines = new ArrayList<>();
        for (Category category : categories) {
            lines.add(category.getId() + DELIMITER + category.getName());
        }
        FileUtil.writeAllLines(categoriesFile, lines);
    }
}
