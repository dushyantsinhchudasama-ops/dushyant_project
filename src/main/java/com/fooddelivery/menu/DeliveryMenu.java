package com.fooddelivery.menu;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.DeliveryPerson;
import com.fooddelivery.service.OrderService;

import java.util.List;
import java.util.Scanner;

public class DeliveryMenu {
    private final DeliveryPerson deliveryPerson;
    private final OrderService orderService;
    private final Scanner scanner;

    public DeliveryMenu(DeliveryPerson deliveryPerson, OrderService orderService, Scanner scanner) {
        this.deliveryPerson = deliveryPerson;
        this.orderService = orderService;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            showDeliveryOptions();
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> viewAssignedOrders();
                case "2" -> acceptNextOrder();
                case "3" -> viewOrderDetails();
                case "4" -> updateOrderStatus();
                case "5" -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void showDeliveryOptions() {
        System.out.println("\n=== Delivery Person Menu ===");
        System.out.println("1. View Assigned Orders");
        System.out.println("2. Accept Order");
        System.out.println("3. View Order Details");
        System.out.println("4. Update Order Status");
        System.out.println("5. Logout");
        System.out.print("Choose an option: ");
    }

    private void viewAssignedOrders() {
        List<Order> orders = orderService.getOrdersByDeliveryPerson(deliveryPerson.getId());
        if (orders.isEmpty()) {
            System.out.println("No orders assigned.");
            return;
        }
        orders.forEach(order -> System.out.printf("%s - status=%s - total=%.2f%n", order.getId(), order.getStatus(), order.getFinalAmount()));
    }

    private void acceptNextOrder() {
        List<Order> orders = orderService.getOrdersByDeliveryPerson(deliveryPerson.getId());
        if (orders.isEmpty()) {
            System.out.println("No orders assigned.");
            return;
        }
        System.out.print("Enter order id to accept: ");
        String orderId = scanner.nextLine().trim();
        try {
            Order order = orderService.getOrderById(orderId);
            if (!deliveryPerson.getId().equals(order.getDeliveryPersonId())) {
                System.out.println("This order is not assigned to you.");
                return;
            }
            System.out.println("Order accepted. Current status: " + order.getStatus());
        } catch (Exception e) {
            System.out.println("Failed to accept order: " + e.getMessage());
        }
    }

    private void viewOrderDetails() {
        System.out.print("Enter order id: ");
        String orderId = scanner.nextLine().trim();
        try {
            Order order = orderService.getOrderById(orderId);
            if (!deliveryPerson.getId().equals(order.getDeliveryPersonId())) {
                System.out.println("This order is not assigned to you.");
                return;
            }
            System.out.println("\nOrder Details");
            System.out.println("Order ID      : " + order.getId());
            System.out.println("Customer ID   : " + order.getCustomerId());
            System.out.println("Status        : " + order.getStatus());
            System.out.println("Placed At     : " + order.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            System.out.println("Delivery Address: " + order.getDeliveryAddress());
            System.out.println("Items:");
            System.out.printf("%-10s %-20s %-8s %-10s%n", "Item ID", "Name", "Qty", "Price");
            System.out.println("------------------------------------------------------------");
            for (com.fooddelivery.model.OrderItem item : order.getItems()) {
                System.out.printf("%-10d %-20s %-8d %-10.2f%n",
                        item.getItemId(), item.getItemName(), item.getQuantity(), item.getUnitPrice());
            }
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-25s %.2f%n", "Original Amount:", order.getOriginalAmount());
            System.out.printf("%-25s %.2f%n", "Discount:", order.getDiscountAmount());
            System.out.printf("%-25s %.2f%n", "Final Amount:", order.getFinalAmount());
        } catch (Exception e) {
            System.out.println("Failed to retrieve order: " + e.getMessage());
        }
    }

    private void updateOrderStatus() {
        System.out.print("Enter order id: ");
        String orderId = scanner.nextLine().trim();
        try {
            Order order = orderService.getOrderById(orderId);
            if (!deliveryPerson.getId().equals(order.getDeliveryPersonId())) {
                System.out.println("This order is not assigned to you.");
                return;
            }
            orderService.advanceOrderStatus(orderId);
            System.out.println("Order status updated.");
        } catch (Exception e) {
            System.out.println("Failed to update status: " + e.getMessage());
        }
    }
}
