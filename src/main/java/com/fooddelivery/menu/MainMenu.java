package com.fooddelivery.menu;

import com.fooddelivery.enums.Role;
import com.fooddelivery.model.AbstractUser;
import com.fooddelivery.service.AdminService;
import com.fooddelivery.service.AuthenticationService;
import com.fooddelivery.service.CartService;
import com.fooddelivery.service.CategoryService;
import com.fooddelivery.service.CustomerService;
import com.fooddelivery.service.DeliveryService;
import com.fooddelivery.service.MenuService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.PaymentService;
import com.fooddelivery.service.StatisticsService;

import java.util.Scanner;

public class MainMenu {
    private final AuthenticationService authenticationService;
    private final AdminService adminService;
    private final CustomerService customerService;
    private final DeliveryService deliveryService;
    private final CategoryService categoryService;
    private final MenuService menuService;
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final StatisticsService statisticsService;

    public MainMenu(AuthenticationService authenticationService,
                    AdminService adminService,
                    CustomerService customerService,
                    DeliveryService deliveryService,
                    CategoryService categoryService,
                    MenuService menuService,
                    CartService cartService,
                    OrderService orderService,
                    PaymentService paymentService,
                    StatisticsService statisticsService) {
        this.authenticationService = authenticationService;
        this.adminService = adminService;
        this.customerService = customerService;
        this.deliveryService = deliveryService;
        this.categoryService = categoryService;
        this.menuService = menuService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.statisticsService = statisticsService;
    }

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                showMainOptions();
                String option = scanner.nextLine().trim();
                switch (option) {
                    case "1" -> handleAdminLogin(scanner);
                    case "2" -> handleCustomerFlow(scanner);
                    case "3" -> handleDeliveryLogin(scanner);
                    case "4" -> running = false;
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
            System.out.println("Thank you for using Food Delivery App. Goodbye!");
        }
    }

    private void showMainOptions() {
        System.out.println("\n=== Food Delivery Application ===");
        System.out.println("1. Admin Login");
        System.out.println("2. Customer Register / Login");
        System.out.println("3. Delivery Person Login");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    private void handleAdminLogin(Scanner scanner) {
        System.out.print("Enter admin email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        try {
            AbstractUser user = authenticationService.login(email, password);
            if (user.getRole() == Role.ADMIN) {
                new AdminMenu(adminService, categoryService, menuService, deliveryService, orderService, statisticsService, scanner).start();
            } else {
                System.out.println("You are not authorized as admin.");
            }
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void handleCustomerFlow(Scanner scanner) {
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.print("Choose an option: ");
        String option = scanner.nextLine().trim();
        if (option.equals("1")) {
            registerCustomer(scanner);
        } else if (option.equals("2")) {
            customerLogin(scanner);
        } else {
            System.out.println("Invalid option.");
        }
    }

    private void registerCustomer(Scanner scanner) {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Phone number: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Address: ");
        String address = scanner.nextLine().trim();
        try {
            var customer = customerService.registerCustomer(name, email, password, phone, address);
            System.out.println("Registration successful. Please login.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void customerLogin(Scanner scanner) {
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        try {
            var user = authenticationService.login(email, password);
            if (user.getRole() == Role.CUSTOMER) {
                new CustomerMenu((com.fooddelivery.model.Customer) user, categoryService, menuService, cartService, orderService, paymentService, authenticationService, customerService, scanner).start();
            } else {
                System.out.println("You are not authorized as customer.");
            }
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void handleDeliveryLogin(Scanner scanner) {
        System.out.print("Enter delivery email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        try {
            var user = authenticationService.login(email, password);
            if (user.getRole() == Role.DELIVERY_PERSON) {
                new DeliveryMenu((com.fooddelivery.model.DeliveryPerson) user, orderService, scanner).start();
            } else {
                System.out.println("You are not authorized as delivery person.");
            }
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }
}
