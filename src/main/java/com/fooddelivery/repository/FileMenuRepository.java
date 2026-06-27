package com.fooddelivery.repository;

import com.fooddelivery.exception.DataAccessException;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.utility.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileMenuRepository implements MenuRepository {
    private static final String DEFAULT_MENU_FILE = "data/menuItems.txt";
    private static final String DELIMITER = "\u001F";
    private final List<MenuItem> menuItems = new ArrayList<>();
    private final String menuFile;

    public FileMenuRepository() {
        this(DEFAULT_MENU_FILE);
    }

    public FileMenuRepository(String menuFile) {
        this.menuFile = menuFile;
        loadMenuItems();
    }

    @Override
    public List<MenuItem> findAll() {
        return new ArrayList<>(menuItems);
    }

    @Override
    public Optional<MenuItem> findById(int id) {
        return menuItems.stream()
                .filter(item -> item.getId() == id)
                .findFirst();
    }

    @Override
    public List<MenuItem> findByCategoryId(int categoryId) {
        return menuItems.stream()
                .filter(item -> item.getCategoryId() == categoryId)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItem> findAvailableItems() {
        return menuItems.stream()
                .filter(MenuItem::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public void save(MenuItem menuItem) {
        if (existsByNameAndCategory(menuItem.getName(), menuItem.getCategoryId())) {
            throw new DataAccessException("Menu item already exists: " + menuItem.getName());
        }
        menuItems.add(menuItem);
        flush();
    }

    @Override
    public void update(MenuItem menuItem) {
        int index = findIndexById(menuItem.getId());
        if (index == -1) {
            throw new DataAccessException("Menu item not found: " + menuItem.getId());
        }
        menuItems.set(index, menuItem);
        flush();
    }

    @Override
    public void deleteById(int id) {
        int index = findIndexById(id);
        if (index == -1) {
            throw new DataAccessException("Menu item not found: " + id);
        }
        menuItems.remove(index);
        flush();
    }

    @Override
    public boolean existsByNameAndCategory(String name, int categoryId) {
        return menuItems.stream()
                .anyMatch(item -> item.getCategoryId() == categoryId && item.getName().equalsIgnoreCase(name));
    }

    private void loadMenuItems() {
        List<String> lines = FileUtil.readAllLines(menuFile);
        menuItems.clear();
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split(DELIMITER, -1);
            if (parts.length != 5) {
                throw new DataAccessException("Invalid menu item record: " + line);
            }
            int id;
            int categoryId;
            try {
                id = Integer.parseInt(parts[0]);
                categoryId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                throw new DataAccessException("Invalid menu item id or category id format: " + line, e);
            }
            String name = parts[2];
            double price;
            try {
                price = Double.parseDouble(parts[3]);
            } catch (NumberFormatException e) {
                throw new DataAccessException("Invalid price format for menu item: " + line, e);
            }
            boolean available = Boolean.parseBoolean(parts[4]);
            menuItems.add(new MenuItem(id, categoryId, name, price, available));
        }
    }

    private int findIndexById(int id) {
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private void flush() {
        List<String> lines = new ArrayList<>();
        for (MenuItem item : menuItems) {
            lines.add(String.join(DELIMITER,
                    String.valueOf(item.getId()),
                    String.valueOf(item.getCategoryId()),
                    item.getName(),
                    String.valueOf(item.getPrice()),
                    String.valueOf(item.isAvailable())
            ));
        }
        FileUtil.writeAllLines(menuFile, lines);
    }
}
