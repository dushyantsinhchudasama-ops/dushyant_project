package com.fooddelivery.menu;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.model.Category;
import com.fooddelivery.model.DeliveryPerson;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.service.AdminService;
import com.fooddelivery.service.CategoryService;
import com.fooddelivery.service.DeliveryService;
import com.fooddelivery.service.MenuService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.StatisticsService;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final AdminService adminService;
    private final CategoryService categoryService;
    private final MenuService menuService;
    private final DeliveryService deliveryService;
    private final OrderService orderService;
    private final StatisticsService statisticsService;
    private final Scanner scanner;

    public AdminMenu(AdminService adminService,
                     CategoryService categoryService,
                     MenuService menuService,
                     DeliveryService deliveryService,
                     OrderService orderService,
                     StatisticsService statisticsService,
                     Scanner scanner) {
        this.adminService = adminService;
        this.categoryService = categoryService;
        this.menuService = menuService;
        this.deliveryService = deliveryService;
        this.orderService = orderService;
        this.statisticsService = statisticsService;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            showAdminOptions();
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> manageCategories();
                case "2" -> manageMenuItems();
                case "3" -> manageDeliveryPersons();
                case "4" -> manageOrders();
                case "5" -> showStatistics();
                case "6" -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void showAdminOptions() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. Category Management");
        System.out.println("2. Menu Management");
        System.out.println("3. Delivery Person Management");
        System.out.println("4. Order Management");
        System.out.println("5. Statistics");
        System.out.println("6. Logout");
        System.out.print("Choose an option: ");
    }

    private void manageCategories() {
        boolean running = true;
        while (running) {
            System.out.println("\n-- Category Management --");
            System.out.println("1. Add Category");
            System.out.println("2. Update Category");
            System.out.println("3. Delete Category");
            System.out.println("4. View Categories");
            System.out.println("5. Back");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> addCategory();
                case "2" -> updateCategory();
                case "3" -> deleteCategory();
                case "4" -> viewCategories();
                case "5" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addCategory() {
        System.out.print("Category name: ");
        String name = scanner.nextLine().trim();
        try {
            Category category = categoryService.addCategory(name);
            System.out.println("Category added: " + category.getName());
        } catch (Exception e) {
            System.out.println("Failed to add category: " + e.getMessage());
        }
    }

    private void updateCategory() {
        viewCategories();
        System.out.print("Enter category id to update: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid category id.");
            return;
        }
        System.out.print("New category name: ");
        String name = scanner.nextLine().trim();
        try {
            Category category = categoryService.updateCategory(id, name);
            System.out.println("Updated category: " + category.getName());
        } catch (Exception e) {
            System.out.println("Failed to update category: " + e.getMessage());
        }
    }

    private void deleteCategory() {
        viewCategories();
        System.out.print("Enter category id to delete: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid category id.");
            return;
        }
        try {
            categoryService.deleteCategory(id);
            System.out.println("Category deleted.");
        } catch (Exception e) {
            System.out.println("Failed to delete category: " + e.getMessage());
        }
    }

    private void viewCategories() {
        List<Category> categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            return;
        }
        System.out.println("Categories:");
        System.out.printf("%-8s %-20s%n", "ID", "Name");
        System.out.println("----------------------------");
        categories.forEach(category -> System.out.printf("%-8d %-20s%n", category.getId(), category.getName()));
    }

    private void manageMenuItems() {
        boolean running = true;
        while (running) {
            System.out.println("\n-- Menu Management --");
            System.out.println("1. Add Menu Item");
            System.out.println("2. Update Menu Item");
            System.out.println("3. Remove Menu Item");
            System.out.println("4. Change Price");
            System.out.println("5. Enable / Disable Item");
            System.out.println("6. View Menu");
            System.out.println("7. Back");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> addMenuItem();
                case "2" -> updateMenuItem();
                case "3" -> removeMenuItem();
                case "4" -> changeMenuItemPrice();
                case "5" -> toggleMenuItemAvailability();
                case "6" -> viewMenuItems();
                case "7" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addMenuItem() {
        viewCategories();
        System.out.print("Enter category id: ");
        int categoryId;
        try {
            categoryId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid category id.");
            return;
        }
        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine().trim();
        System.out.print("Enter price: ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Is available? true/false: ");
        boolean available = Boolean.parseBoolean(scanner.nextLine().trim());
        try {
            MenuItem item = menuService.addMenuItem(categoryId, itemName, price, available);
            System.out.println("Menu item added: " + item.getName());
        } catch (Exception e) {
            System.out.println("Failed to add menu item: " + e.getMessage());
        }
    }

    private void updateMenuItem() {
        viewMenuItems();
        System.out.print("Enter item id to update: ");
        int itemId;
        try {
            itemId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid item id.");
            return;
        }
        System.out.print("Enter new name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter new price: ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Is available? true/false: ");
        boolean available = Boolean.parseBoolean(scanner.nextLine().trim());
        try {
            MenuItem item = menuService.updateMenuItem(itemId, name, price, available);
            System.out.println("Menu item updated: " + item.getName());
        } catch (Exception e) {
            System.out.println("Failed to update menu item: " + e.getMessage());
        }
    }

    private void removeMenuItem() {
        viewMenuItems();
        System.out.print("Enter item id to remove: ");
        int itemId;
        try {
            itemId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid item id.");
            return;
        }
        try {
            menuService.removeMenuItem(itemId);
            System.out.println("Menu item removed.");
        } catch (Exception e) {
            System.out.println("Failed to remove item: " + e.getMessage());
        }
    }

    private void changeMenuItemPrice() {
        viewMenuItems();
        System.out.print("Enter item id to change price: ");
        int itemId;
        try {
            itemId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid item id.");
            return;
        }
        System.out.print("Enter new price: ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        try {
            MenuItem item = menuService.changePrice(itemId, price);
            System.out.println("Price updated for " + item.getName() + " to " + item.getPrice());
        } catch (Exception e) {
            System.out.println("Failed to change price: " + e.getMessage());
        }
    }

    private void toggleMenuItemAvailability() {
        viewMenuItems();
        System.out.print("Enter item id to toggle availability: ");
        int itemId;
        try {
            itemId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid item id.");
            return;
        }
        try {
            MenuItem item = menuService.getMenuItemById(itemId);
            MenuItem updated = menuService.setAvailability(itemId, !item.isAvailable());
            System.out.println("Item availability updated: " + updated.isAvailable());
        } catch (Exception e) {
            System.out.println("Failed to update availability: " + e.getMessage());
        }
    }

    private void viewMenuItems() {
        List<MenuItem> items = menuService.getAllMenuItems();
        if (items.isEmpty()) {
            System.out.println("No menu items available.");
            return;
        }
        System.out.println("Menu items:");
        System.out.printf("%-8s %-20s %-12s %-12s %-20s%n", "ID", "Name", "Price", "Available", "Category");
        System.out.println("-------------------------------------------------------------------");
        items.forEach(item -> {
            String categoryName = categoryService.getAllCategories().stream()
                    .filter(category -> category.getId() == item.getCategoryId())
                    .map(Category::getName)
                    .findFirst()
                    .orElse("Unknown");
            System.out.printf("%-8d %-20s %-12.2f %-12s %-20s%n",
                    item.getId(), item.getName(), item.getPrice(), item.isAvailable(), categoryName);
        });
    }

    private void manageDeliveryPersons() {
        boolean running = true;
        while (running) {
            System.out.println("\n-- Delivery Person Management --");
            System.out.println("1. Add Delivery Person");
            System.out.println("2. Update Delivery Person");
            System.out.println("3. Remove Delivery Person");
            System.out.println("4. View Delivery Persons");
            System.out.println("5. Back");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> addDeliveryPerson();
                case "2" -> updateDeliveryPerson();
                case "3" -> removeDeliveryPerson();
                case "4" -> viewDeliveryPersons();
                case "5" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addDeliveryPerson() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Phone number: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Vehicle number: ");
        String vehicle = scanner.nextLine().trim();
        try {
            DeliveryPerson deliveryPerson = deliveryService.addDeliveryPerson(name, email, password, phone, vehicle);
            System.out.println("Delivery person added: " + deliveryPerson.getName());
        } catch (Exception e) {
            System.out.println("Failed to add delivery person: " + e.getMessage());
        }
    }

    private void updateDeliveryPerson() {
        viewDeliveryPersons();
        System.out.print("Enter delivery person id to update: ");
        String id = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone number: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Vehicle number: ");
        String vehicle = scanner.nextLine().trim();
        System.out.print("Is available? true/false: ");
        boolean available = Boolean.parseBoolean(scanner.nextLine().trim());
        try {
            DeliveryPerson updated = deliveryService.updateDeliveryPerson(id, name, email, phone, vehicle, available);
            System.out.println("Updated: " + updated.getName());
        } catch (Exception e) {
            System.out.println("Failed to update: " + e.getMessage());
        }
    }

    private void removeDeliveryPerson() {
        viewDeliveryPersons();
        System.out.print("Enter delivery person id to remove: ");
        String id = scanner.nextLine().trim();
        try {
            deliveryService.removeDeliveryPerson(id);
            System.out.println("Delivery person removed.");
        } catch (Exception e) {
            System.out.println("Failed to remove: " + e.getMessage());
        }
    }

    private void viewDeliveryPersons() {
        List<DeliveryPerson> deliveryPeople = deliveryService.getAllDeliveryPersons();
        if (deliveryPeople.isEmpty()) {
            System.out.println("No delivery persons available.");
            return;
        }
        System.out.println("Delivery persons:");
        System.out.printf("%-12s %-20s %-25s %-15s %-10s%n", "ID", "Name", "Email", "Vehicle", "Available");
        System.out.println("-------------------------------------------------------------------");
        deliveryPeople.forEach(person -> System.out.printf("%-12s %-20s %-25s %-15s %-10s%n",
                person.getId(), person.getName(), person.getEmail(), person.getVehicleNumber(), person.isAvailable()));
    }

    private void manageOrders() {
        boolean running = true;
        while (running) {
            System.out.println("\n-- Order Management --");
            System.out.println("1. View Pending Orders");
            System.out.println("2. View Order History");
            System.out.println("3. View Order Details");
            System.out.println("4. Accept Order");
            System.out.println("5. Reject Order");
            System.out.println("6. Mark Order Ready");
            System.out.println("7. Assign Delivery Person");
            System.out.println("8. Cancel Order");
            System.out.println("9. Back");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> viewPendingOrders();
                case "2" -> viewOrderHistory();
                case "3" -> viewOrderDetails();
                case "4" -> acceptOrder();
                case "5" -> rejectOrder();
                case "6" -> markOrderReady();
                case "7" -> assignDeliveryPerson();
                case "8" -> cancelOrder();
                case "9" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void acceptOrder() {
        viewPendingOrders();
        System.out.print("Enter order id to accept: ");
        String orderId = scanner.nextLine().trim();
        try {
            Order updated = orderService.acceptOrder(orderId);
            System.out.println("Order accepted: " + updated.getId());
        } catch (Exception e) {
            System.out.println("Failed to accept order: " + e.getMessage());
        }
    }

    private void rejectOrder() {
        viewPendingOrders();
        System.out.print("Enter order id to reject: ");
        String orderId = scanner.nextLine().trim();
        try {
            Order updated = orderService.rejectOrder(orderId);
            System.out.println("Order rejected: " + updated.getId());
        } catch (Exception e) {
            System.out.println("Failed to reject order: " + e.getMessage());
        }
    }

    private void markOrderReady() {
        viewPendingOrders();
        System.out.print("Enter order id to mark ready: ");
        String orderId = scanner.nextLine().trim();
        try {
            Order updated = orderService.markReady(orderId);
            System.out.println("Order marked ready: " + updated.getId());
        } catch (Exception e) {
            System.out.println("Failed to mark order ready: " + e.getMessage());
        }
    }

    private void viewPendingOrders() {
        List<Order> orders = orderService.getAllOrders();
        orders.removeIf(o -> !(o.getStatus() == com.fooddelivery.enums.OrderStatus.PLACED || o.getStatus() == com.fooddelivery.enums.OrderStatus.PREPARING));
        if (orders.isEmpty()) {
            System.out.println("No pending orders.");
            return;
        }
        System.out.println("Pending orders:");
        System.out.printf("%-15s %-15s %-15s %-10s%n", "Order ID", "Customer", "Status", "Total");
        System.out.println("------------------------------------------------------");
        orders.forEach(order -> System.out.printf("%-15s %-15s %-15s %-10.2f%n",
                order.getId(), order.getCustomerId(), order.getStatus(), order.getFinalAmount()));
    }

    private void viewOrderHistory() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        System.out.println("Order history:");
        System.out.printf("%-15s %-15s %-15s %-10s%n", "Order ID", "Customer", "Status", "Total");
        System.out.println("------------------------------------------------------");
        orders.forEach(order -> System.out.printf("%-15s %-15s %-15s %-10.2f%n",
                order.getId(), order.getCustomerId(), order.getStatus(), order.getFinalAmount()));
    }

    private void viewAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        System.out.println("Orders:");
        System.out.printf("%-15s %-15s %-15s %-15s %-10s%n", "Order ID", "Customer", "Delivery", "Status", "Total");
        System.out.println("-------------------------------------------------------------------");
        orders.forEach(order -> System.out.printf("%-15s %-15s %-15s %-15s %-10.2f%n",
                order.getId(), order.getCustomerId(), order.getDeliveryPersonId(), order.getStatus(), order.getFinalAmount()));
    }

    private void viewOrderDetails() {
        viewAllOrders();
        System.out.print("Enter order id: ");
        String id = scanner.nextLine().trim();
        try {
            Order order = orderService.getOrderById(id);
            System.out.println(order);
            order.getItems().forEach(item -> System.out.println("  " + item));
        } catch (Exception e) {
            System.out.println("Failed to retrieve order: " + e.getMessage());
        }
    }

    private void assignDeliveryPerson() {
        viewAllOrders();
        System.out.print("Enter order id: ");
        String orderId = scanner.nextLine().trim();
        viewDeliveryPersons();
        System.out.print("Enter delivery person id (leave blank to auto-assign to a free person): ");
        String deliveryId = scanner.nextLine().trim();
        try {
            Order updated = orderService.assignDeliveryPerson(orderId, deliveryId);
            System.out.println("Assigned delivery person to order " + updated.getId());
        } catch (Exception e) {
            System.out.println("Failed to assign delivery person: " + e.getMessage());
        }
    }

    private void cancelOrder() {
        viewAllOrders();
        System.out.print("Enter order id to cancel: ");
        String orderId = scanner.nextLine().trim();
        try {
            Order canceled = orderService.cancelOrder(orderId);
            System.out.println("Order cancelled: " + canceled.getId());
        } catch (Exception e) {
            System.out.println("Failed to cancel order: " + e.getMessage());
        }
    }

    private void showStatistics() {
        System.out.println("\n-- Restaurant Statistics --");
        System.out.println("Total revenue: " + statisticsService.getTotalRevenue());
        System.out.println("Today's revenue: " + statisticsService.getTodaysRevenue());
        System.out.println("Total orders: " + statisticsService.getTotalOrders());
        System.out.println("Delivered orders: " + statisticsService.getDeliveredOrders());
        System.out.println("Cancelled orders: " + statisticsService.getCancelledOrders());
        statisticsService.getMostOrderedItem().ifPresent(item -> System.out.println("Most ordered item: " + item));
        statisticsService.getMostActiveCustomer().ifPresent(customer -> System.out.println("Most active customer: " + customer));
    }
}
