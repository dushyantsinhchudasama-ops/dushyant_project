package com.fooddelivery;

import com.fooddelivery.menu.MainMenu;
import com.fooddelivery.observer.ConsoleNotificationObserver;
import com.fooddelivery.observer.EmailNotificationObserver;
import com.fooddelivery.observer.OrderStatusNotifier;
import com.fooddelivery.observer.SmsNotificationObserver;
import com.fooddelivery.repository.FileCategoryRepository;
import com.fooddelivery.repository.FileMenuRepository;
import com.fooddelivery.repository.FileOrderRepository;
import com.fooddelivery.repository.FileUserRepository;
import com.fooddelivery.repository.UserRepository;
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
import com.fooddelivery.service.TieredDiscountPolicy;

public class Application {
    public static void main(String[] args) {
        UserRepository userRepository = new FileUserRepository();
        var categoryRepository = new FileCategoryRepository();
        var menuRepository = new FileMenuRepository();
        var orderRepository = new FileOrderRepository();

        AuthenticationService authenticationService = new AuthenticationService(userRepository);
        AdminService adminService = new AdminService(userRepository);
        CustomerService customerService = new CustomerService(userRepository);
        DeliveryService deliveryService = new DeliveryService(userRepository);

        CategoryService categoryService = new CategoryService(categoryRepository);
        MenuService menuService = new MenuService(menuRepository, categoryRepository);
        CartService cartService = new CartService(menuRepository);
        PaymentService paymentService = new PaymentService(new TieredDiscountPolicy());
        StatisticsService statisticsService = new StatisticsService(orderRepository, userRepository);

        OrderStatusNotifier notifier = new OrderStatusNotifier();
        notifier.registerObserver(new ConsoleNotificationObserver());
        notifier.registerObserver(new EmailNotificationObserver());
        notifier.registerObserver(new SmsNotificationObserver());

        OrderService orderService = new OrderService(orderRepository, paymentService, notifier, deliveryService);

        adminService.initializeDefaultAdmin("Super Admin", "admin@fooddelivery.com", "admin123");

        MainMenu mainMenu = new MainMenu(authenticationService, adminService, customerService, deliveryService,
                categoryService, menuService, cartService, orderService, paymentService, statisticsService);
        mainMenu.start();
        
    }
}
