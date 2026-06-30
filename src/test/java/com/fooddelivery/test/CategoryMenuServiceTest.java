package com.fooddelivery.test;

import com.fooddelivery.exception.CategoryNotFoundException;
import com.fooddelivery.exception.MenuItemNotFoundException;
import com.fooddelivery.exception.AlreadyExistsException;
import com.fooddelivery.model.Category;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.repository.CategoryRepository;
import com.fooddelivery.repository.FileCategoryRepository;
import com.fooddelivery.repository.FileMenuRepository;
import com.fooddelivery.repository.MenuRepository;
import com.fooddelivery.service.CategoryService;
import com.fooddelivery.service.MenuService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryMenuServiceTest {
    private static final Path CATEGORY_FILE = Paths.get("data", "categories-test.txt");
    private static final Path MENU_FILE = Paths.get("data", "menuItems-test.txt");

    private CategoryRepository categoryRepository;
    private MenuRepository menuRepository;
    private CategoryService categoryService;
    private MenuService menuService;

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(CATEGORY_FILE.getParent());
        Files.deleteIfExists(CATEGORY_FILE);
        Files.deleteIfExists(MENU_FILE);
        Files.createFile(CATEGORY_FILE);
        Files.createFile(MENU_FILE);
        categoryRepository = new FileCategoryRepository(CATEGORY_FILE.toString());
        menuRepository = new FileMenuRepository(MENU_FILE.toString());
        categoryService = new CategoryService(categoryRepository);
        menuService = new MenuService(menuRepository, categoryRepository);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(CATEGORY_FILE);
        Files.deleteIfExists(MENU_FILE);
    }

    @Test
    void shouldAddAndRetrieveCategory() {
        Category category = categoryService.addCategory("Indian");
        assertNotNull(category.getId());
        assertEquals("Indian", category.getName());
        assertEquals(1, categoryService.getAllCategories().size());
    }

    @Test
    void shouldNotAllowDuplicateCategory() {
        categoryService.addCategory("Italian");
        assertThrows(AlreadyExistsException.class, () -> categoryService.addCategory("Italian"));
    }

    @Test
    void shouldUpdateCategory() {
        Category category = categoryService.addCategory("Fast Food");
        Category updated = categoryService.updateCategory(category.getId(), "Fastfood");
        assertEquals("Fastfood", updated.getName());
    }

    @Test
    void shouldAddMenuItemAndFindByCategory() {
        Category category = categoryService.addCategory("Chinese");
        MenuItem item = menuService.addMenuItem(category.getId(), "Noodles", 120.0, true);
        assertNotNull(item.getId());
        assertEquals(1, menuService.getMenuItemsByCategory(category.getId()).size());
    }

    @Test
    void shouldNotAllowMenuItemWithoutCategory() {
        assertThrows(CategoryNotFoundException.class,
                () -> menuService.addMenuItem(-1, "Sushi", 200.0, true));
    }

    @Test
    void shouldChangeMenuItemPrice() {
        Category category = categoryService.addCategory("Beverages");
        MenuItem item = menuService.addMenuItem(category.getId(), "Lassi", 80.0, true);
        MenuItem updated = menuService.changePrice(item.getId(), 95.0);
        assertEquals(95.0, updated.getPrice());
    }

    @Test
    void shouldRemoveMenuItem() {
        Category category = categoryService.addCategory("Desserts");
        MenuItem item = menuService.addMenuItem(category.getId(), "Gulab Jamun", 50.0, true);
        menuService.removeMenuItem(item.getId());
        assertThrows(MenuItemNotFoundException.class, () -> menuService.getMenuItemById(item.getId()));
    }
}
