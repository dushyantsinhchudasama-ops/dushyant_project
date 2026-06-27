package com.fooddelivery.service;

import com.fooddelivery.exception.CategoryNotFoundException;
import com.fooddelivery.exception.MenuItemNotFoundException;
import com.fooddelivery.exception.UserAlreadyExistsException;
import com.fooddelivery.model.Category;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.repository.CategoryRepository;
import com.fooddelivery.repository.MenuRepository;

import java.util.List;

public class MenuService {
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    public MenuService(MenuRepository menuRepository, CategoryRepository categoryRepository) {
        this.menuRepository = menuRepository;
        this.categoryRepository = categoryRepository;
    }

    public MenuItem addMenuItem(int categoryId, String name, double price, boolean available) {
        validateCategoryExists(categoryId);
        if (menuRepository.existsByNameAndCategory(name, categoryId)) {
            throw new UserAlreadyExistsException("Menu item already exists in this category: " + name);
        }
        int id = getNextMenuItemId();
        MenuItem menuItem = new MenuItem(id, categoryId, name, price, available);
        menuRepository.save(menuItem);
        return menuItem;
    }

    public MenuItem updateMenuItem(int itemId, String name, double price, boolean available) {
        MenuItem menuItem = menuRepository.findById(itemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found: " + itemId));

        if (!menuItem.getName().equalsIgnoreCase(name) && menuRepository.existsByNameAndCategory(name, menuItem.getCategoryId())) {
            throw new UserAlreadyExistsException("Menu item already exists in this category: " + name);
        }

        menuItem.setName(name);
        menuItem.setPrice(price);
        menuItem.setAvailable(available);
        menuRepository.update(menuItem);
        return menuItem;
    }

    public void removeMenuItem(int itemId) {
        if (menuRepository.findById(itemId).isEmpty()) {
            throw new MenuItemNotFoundException("Menu item not found: " + itemId);
        }
        menuRepository.deleteById(itemId);
    }

    public MenuItem changePrice(int itemId, double newPrice) {
        MenuItem menuItem = menuRepository.findById(itemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found: " + itemId));
        menuItem.setPrice(newPrice);
        menuRepository.update(menuItem);
        return menuItem;
    }

    public MenuItem setAvailability(int itemId, boolean available) {
        MenuItem menuItem = menuRepository.findById(itemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found: " + itemId));
        menuItem.setAvailable(available);
        menuRepository.update(menuItem);
        return menuItem;
    }

    public List<MenuItem> getAllMenuItems() {
        return menuRepository.findAll();
    }

    public List<MenuItem> getMenuItemsByCategory(int categoryId) {
        validateCategoryExists(categoryId);
        return menuRepository.findByCategoryId(categoryId);
    }

    public List<MenuItem> getAvailableMenuItems() {
        return menuRepository.findAvailableItems();
    }

    public MenuItem getMenuItemById(int itemId) {
        return menuRepository.findById(itemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found: " + itemId));
    }

    private void validateCategoryExists(int categoryId) {
        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new CategoryNotFoundException("Category not found: " + categoryId);
        }
    }

    private int getNextMenuItemId() {
        return menuRepository.findAll().stream()
                .mapToInt(MenuItem::getId)
                .max()
                .orElse(0) + 1;
    }
}
