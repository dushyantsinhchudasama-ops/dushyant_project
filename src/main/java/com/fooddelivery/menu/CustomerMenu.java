package com.fooddelivery.menu;

import com.fooddelivery.enums.PaymentType;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.CartItem;
import com.fooddelivery.model.Category;
import com.fooddelivery.model.Customer;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.service.AuthenticationService;
import com.fooddelivery.service.CartService;
import com.fooddelivery.service.CategoryService;
import com.fooddelivery.service.CustomerService;
import com.fooddelivery.service.DeliveryService;
import com.fooddelivery.service.MenuService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.PaymentService;
import com.fooddelivery.strategy.CardPaymentStrategy;
import com.fooddelivery.strategy.CashPaymentStrategy;
import com.fooddelivery.strategy.UpiPaymentStrategy;

import java.util.List;
import java.util.Scanner;

public class CustomerMenu {
    private final Customer customer;
    private final CategoryService categoryService;
    private final MenuService menuService;
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;
    private final AuthenticationService authenticationService;
    private final CustomerService customerService;
    private final Scanner scanner;

    public CustomerMenu(Customer customer,
                        CategoryService categoryService,
                        MenuService menuService,
                        CartService cartService,
                        OrderService orderService,
                        PaymentService paymentService,
                        DeliveryService deliveryService,
                        AuthenticationService authenticationService,
                        CustomerService customerService,
                        Scanner scanner) {
        this.customer = customer;
        this.categoryService = categoryService;
        this.menuService = menuService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.deliveryService = deliveryService;
        this.authenticationService = authenticationService;
        this.customerService = customerService;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            showCustomerOptions();
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> viewProfile();
                case "2" -> updateProfile();
                case "3" -> changePassword();
                case "4" -> viewCategories();
                case "5" -> viewMenu();
                case "6" -> manageCart();
                case "7" -> checkout();
                case "8" -> viewCurrentOrders();
                case "9" -> viewOrderHistory();
                case "10" -> orderAgain();
                case "11" -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void showCustomerOptions() {
        System.out.println("\n=== Customer Menu ===");
        System.out.println("1. View Profile");
        System.out.println("2. Update Profile");
        System.out.println("3. Change Password");
        System.out.println("4. View Categories");
        System.out.println("5. View Menu");
        System.out.println("6. Cart");
        System.out.println("7. Checkout");
        System.out.println("8. Track Current Orders");
        System.out.println("9. View Order History");
        System.out.println("10. Order Again");
        System.out.println("11. Logout");
        System.out.print("Choose an option: ");
    }

    private void viewProfile() {
        System.out.println("\nCustomer Profile:");
        System.out.println("Name: " + customer.getName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Phone: " + customer.getPhoneNumber());
        System.out.println("House No: " + customer.getHouseNo());
        System.out.println("Main Address: " + customer.getMainAddress());
        System.out.println("Pincode: " + customer.getPincode());
    }

    private void updateProfile() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("House No: ");
        String houseNo = scanner.nextLine().trim();
        System.out.print("Main Address: ");
        String mainAddress = scanner.nextLine().trim();
        System.out.print("Pincode: ");
        String pincode = scanner.nextLine().trim();
        try {
            Customer updated = customerService.updateProfile(customer.getId(), name, email, phone, houseNo, mainAddress, pincode);
            System.out.println("Profile updated.");
        } catch (Exception e) {
            System.out.println("Failed to update profile: " + e.getMessage());
        }
    }

    private void changePassword() {
        System.out.print("Current password: ");
        String current = scanner.nextLine().trim();
        System.out.print("New password: ");
        String next = scanner.nextLine().trim();
        try {
            authenticationService.changePassword(customer.getId(), current, next);
            System.out.println("Password changed successfully.");
        } catch (Exception e) {
            System.out.println("Failed to change password: " + e.getMessage());
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

    private void viewMenu() {
        viewCategories();
        System.out.print("Enter category id to view menu or press enter for all: ");
        String categoryIdInput = scanner.nextLine().trim();
        List<MenuItem> items;
        if (categoryIdInput.isBlank()) {
            items = menuService.getAvailableMenuItems();
        } else {
            int categoryId;
            try {
                categoryId = Integer.parseInt(categoryIdInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid category id.");
                return;
            }
            items = menuService.getMenuItemsByCategory(categoryId).stream()
                    .filter(MenuItem::isAvailable)
                    .toList();
        }
        if (items.isEmpty()) {
            System.out.println("No menu items available.");
            return;
        }
        System.out.println("Menu items:");
        System.out.printf("%-8s %-20s %-12s %-20s%n", "ID", "Name", "Price", "Category");
        System.out.println("---------------------------------------------------");
        items.forEach(item -> {
            String categoryName = categoryService.getAllCategories().stream()
                    .filter(category -> category.getId() == item.getCategoryId())
                    .map(Category::getName)
                    .findFirst()
                    .orElse("Unknown");
            System.out.printf("%-8d %-20s %-12.2f %-20s%n", item.getId(), item.getName(), item.getPrice(), categoryName);
        });
    }

    private void manageCart() {
        boolean running = true;
        while (running) {
            System.out.println("\n-- Cart Menu --");
            System.out.println("1. Add Item");
            System.out.println("2. Remove Item");
            System.out.println("3. Update Quantity");
            System.out.println("4. Clear Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Back");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> addItemToCart();
                case "2" -> removeItemFromCart();
                case "3" -> updateCartQuantity();
                case "4" -> clearCart();
                case "5" -> viewCart();
                case "6" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addItemToCart() {
        viewMenu();
        System.out.print("Enter item id: ");
        int itemId;
        try {
            itemId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid item id.");
            return;
        }
        System.out.print("Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine().trim());
        try {
            cartService.addItemToCart(customer.getId(), itemId, quantity);
            System.out.println("Item added to cart.");
        } catch (Exception e) {
            System.out.println("Failed to add item: " + e.getMessage());
        }
    }

    private void removeItemFromCart() {
        viewCart();
        System.out.print("Enter item id to remove: ");
        int itemId;
        try {
            itemId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid item id.");
            return;
        }
        try {
            cartService.removeItem(customer.getId(), itemId);
            System.out.println("Item removed.");
        } catch (Exception e) {
            System.out.println("Failed to remove item: " + e.getMessage());
        }
    }

    private void updateCartQuantity() {
        viewCart();
        System.out.print("Enter item id: ");
        int itemId;
        try {
            itemId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid item id.");
            return;
        }
        System.out.print("Enter new quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine().trim());
        try {
            cartService.updateItemQuantity(customer.getId(), itemId, quantity);
            System.out.println("Quantity updated.");
        } catch (Exception e) {
            System.out.println("Failed to update quantity: " + e.getMessage());
        }
    }

    private void clearCart() {
        cartService.clearCart(customer.getId());
        System.out.println("Cart cleared.");
    }

    private void viewCart() {
        Cart cart = cartService.viewCart(customer.getId());
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        System.out.println("Cart items:");
        System.out.printf("%-20s %-10s %-12s%n", "Item", "Qty", "Subtotal");
        System.out.println("------------------------------------");
        for (CartItem item : cart.getItems()) {
            System.out.printf("%-20s %-10d %-12.2f%n", item.getMenuItem().getName(), item.getQuantity(), item.getSubtotal());
        }
        System.out.printf("%-20s %-10s %-12.2f%n", "Total", "", cart.getTotalAmount());
    }

    private void checkout() {
        Cart cart = cartService.viewCart(customer.getId());
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        System.out.println("Deliver to current address? (Y/N)");
        System.out.print("Option: ");
        String deliveryOption = scanner.nextLine().trim();
        String houseNo = customer.getHouseNo();
        String mainAddress = customer.getMainAddress();
        String pincode = customer.getPincode();
        if (!deliveryOption.equalsIgnoreCase("Y")) {
            System.out.println("Enter delivery Details: ");
            System.out.print("Enter House No: ");
            houseNo = scanner.nextLine().trim();
            System.out.print("Enter Main address: ");
            mainAddress = scanner.nextLine().trim();
            System.out.print("Enter Pincode: ");
            pincode = scanner.nextLine().trim();
            if (houseNo.isBlank()) {
                houseNo = customer.getHouseNo();
            }
            if (mainAddress.isBlank()) {
                mainAddress = customer.getMainAddress();
            }
            if (pincode.isBlank()) {
                pincode = customer.getPincode();
            }
        }

        System.out.println("Choose payment method:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. UPI");
        System.out.print("Option: ");
        String option = scanner.nextLine().trim();
        try {
            var strategy = switch (option) {
                case "1" -> new CashPaymentStrategy(paymentService.getDiscountPolicy());
                case "2" -> new CardPaymentStrategy(paymentService.getDiscountPolicy());
                case "3" -> new UpiPaymentStrategy(paymentService.getDiscountPolicy());
                default -> throw new IllegalArgumentException("Invalid payment option.");
            };
            Order order = orderService.placeOrder(customer.getId(), houseNo, mainAddress, pincode, cart, strategy);
            cartService.clearCart(customer.getId());
            System.out.println("Order placed successfully: " + order.getId());
            System.out.println("Total payable: " + order.getFinalAmount());
        } catch (Exception e) {
            System.out.println("Checkout failed: " + e.getMessage());
        }
    }

    private void viewCurrentOrders() {
        List<Order> orders = orderService.getOrdersByCustomer(customer.getId());
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        System.out.println("Current orders:");
        System.out.printf("%-15s %-15s %-10s%n", "Order ID", "Status", "Total");
        System.out.println("-----------------------------------");
        orders.stream()
                .filter(order -> order.getStatus() != com.fooddelivery.enums.OrderStatus.DELIVERED && order.getStatus() != com.fooddelivery.enums.OrderStatus.CANCELLED)
                .forEach(order -> System.out.printf("%-15s %-15s %-10.2f%n", order.getId(), order.getStatus(), order.getFinalAmount()));
    }

    private void viewOrderHistory() {
        List<Order> orders = orderService.getOrdersByCustomer(customer.getId());
        if (orders.isEmpty()) {
            System.out.println("No order history available.");
            return;
        }

        System.out.println("Order history:");
        System.out.printf("%-15s %-15s %-10s%n", "Order ID", "Status", "Total");
        System.out.println("-----------------------------------");
        orders.forEach(order -> System.out.printf("%-15s %-15s %-10.2f%n", order.getId(), order.getStatus(), order.getFinalAmount()));

        System.out.print("Enter order id to view details (press enter to return to customer menu): ");
        String orderId = scanner.nextLine().trim();
        if (orderId.isBlank()) {
            return;
        }

        try {
            Order selectedOrder = orderService.getOrderById(orderId);
            if (!selectedOrder.getCustomerId().equals(customer.getId())) {
                System.out.println("This order does not belong to you.");
                return;
            }
            showOrderDetails(selectedOrder);
        } catch (Exception e) {
            System.out.println("Invalid order id: " + e.getMessage());
        }
    }

    private void showOrderDetails(Order order) {
        String deliveryPersonName = "Not assigned";
        if (order.getDeliveryPersonId() != null && !order.getDeliveryPersonId().isBlank()) {
            try {
                deliveryPersonName = deliveryService.getDeliveryPersonById(order.getDeliveryPersonId()).getName();
            } catch (Exception ignored) {
                deliveryPersonName = order.getDeliveryPersonId();
            }
        }

        System.out.println("\nOrder Details");
        System.out.println("Order ID     : " + order.getId());
        System.out.println("Status       : " + order.getStatus());
        System.out.println("Placed At    : " + order.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        System.out.println("Delivery Address: " + order.getDeliveryAddress());
        System.out.println("Delivery Person: " + deliveryPersonName);
        System.out.println("Items:");
        System.out.printf("%-10s %-20s %-8s %-10s%n", "Item ID", "Name", "Qty", "Price");
        System.out.println("------------------------------------------------------------");
        for (OrderItem item : order.getItems()) {
            System.out.printf("%-10d %-20s %-8d %-10.2f%n",
                    item.getItemId(), item.getItemName(), item.getQuantity(), item.getUnitPrice());
        }
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-25s %.2f%n", "Original Amount:", order.getOriginalAmount());
        System.out.printf("%-25s %.2f%n", "Discount:", order.getDiscountAmount());
        System.out.printf("%-25s %.2f%n", "Final Amount:", order.getFinalAmount());
    }

    private void orderAgain() {
        viewOrderHistory();
        System.out.print("Enter order id to reorder: ");
        String orderId = scanner.nextLine().trim();
        try {
            Order order = orderService.getOrderById(orderId);
            for (OrderItem item : order.getItems()) {
                try {
                    cartService.addItemToCart(customer.getId(), item.getItemId(), item.getQuantity());
                } catch (Exception ignored) {
                    // Skip unavailable items
                }
            }
            System.out.println("Order items added to cart.");
        } catch (Exception e) {
            System.out.println("Failed to reorder: " + e.getMessage());
        }
    }
}
