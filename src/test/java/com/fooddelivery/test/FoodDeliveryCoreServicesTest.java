package com.fooddelivery.test;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.enums.PaymentType;
import com.fooddelivery.enums.Role;
import com.fooddelivery.factory.UserFactory;
import com.fooddelivery.model.Admin;
import com.fooddelivery.model.Bill;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.Customer;
import com.fooddelivery.model.DeliveryPerson;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.observer.OrderObserver;
import com.fooddelivery.observer.OrderStatusNotifier;
import com.fooddelivery.repository.FileMenuRepository;
import com.fooddelivery.repository.FileOrderRepository;
import com.fooddelivery.repository.FileUserRepository;
import com.fooddelivery.repository.MenuRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.CartService;
import com.fooddelivery.service.DeliveryService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.PaymentService;
import com.fooddelivery.service.TieredDiscountPolicy;
import com.fooddelivery.strategy.PaymentStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FoodDeliveryCoreServicesTest {
    @TempDir
    Path tempDir;

    @Test
    void cartServiceShouldAddUpdateAndRemoveItems() {
        MenuRepository menuRepository = new FileMenuRepository(tempDir.resolve("menu.txt").toString());
        MenuItem pizza = new MenuItem(1, 1, "Pizza", 200.0, true);
        MenuItem pasta = new MenuItem(2, 1, "Pasta", 150.0, false);
        menuRepository.save(pizza);
        menuRepository.save(pasta);

        CartService cartService = new CartService(menuRepository);

        cartService.addItemToCart("cust-1", 1, 2);
        Cart cart = cartService.viewCart("cust-1");
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());

        cartService.updateItemQuantity("cust-1", 1, 5);
        assertEquals(5, cartService.viewCart("cust-1").getItems().get(0).getQuantity());

        assertThrows(IllegalArgumentException.class, () -> cartService.addItemToCart("cust-1", 2, 1));

        cartService.removeItem("cust-1", 1);
        assertTrue(cartService.viewCart("cust-1").isEmpty());
    }

    @Test
    void deliveryServiceShouldManageAvailability() {
        UserRepository userRepository = new FileUserRepository(tempDir.resolve("users.txt").toString());
        DeliveryService deliveryService = new DeliveryService(userRepository);

        DeliveryPerson person = deliveryService.addDeliveryPerson("Ravi", "ravi@example.com", "pass", "9999999999", "KA01AB1234");
        assertTrue(person.isAvailable());
        assertEquals(person.getId(), deliveryService.findAvailableDeliveryPerson().getId());

        deliveryService.updateDeliveryPerson(person.getId(), person.getName(), person.getEmail(), person.getPhoneNumber(), person.getVehicleNumber(), false);
        assertThrows(IllegalStateException.class, deliveryService::findAvailableDeliveryPerson);
    }

    @Test
    void userFactoryShouldCreateExpectedUserTypes() {
        Admin admin = UserFactory.createAdmin("1", "Asha", "asha@example.com", "pass");
        Customer customer = UserFactory.createCustomer("2", "Nina", "nina@example.com", "pass", "1111111111", "Road 1");
        DeliveryPerson deliveryPerson = UserFactory.createDeliveryPerson("3", "Rohan", "rohan@example.com", "pass", "2222222222", "KA02CD3456");

        assertEquals(Role.ADMIN, admin.getRole());
        assertEquals(Role.CUSTOMER, customer.getRole());
        assertEquals(Role.DELIVERY_PERSON, deliveryPerson.getRole());
        assertEquals("Asha", admin.getName());
        assertEquals("Road 1", customer.getAddress());
        assertTrue(deliveryPerson.isAvailable());
    }

    @Test
    void orderStatusNotifierShouldNotifyRegisteredObservers() {
        OrderStatusNotifier notifier = new OrderStatusNotifier();
        List<String> notifications = new ArrayList<>();
        OrderObserver observer = order -> notifications.add(order.getId());
        notifier.registerObserver(observer);

        Order order = new Order("10", "cust-1", null, List.of(), 100.0, 0.0, 0.0, 100.0, PaymentType.CASH, OrderStatus.PLACED, java.time.LocalDateTime.now());
        notifier.notifyObservers(order);

        assertEquals(List.of("10"), notifications);
    }

    @Test
    void orderServiceShouldPlaceAssignAndCompleteOrder() {
        UserRepository userRepository = new FileUserRepository(tempDir.resolve("users-order.txt").toString());
        MenuRepository menuRepository = new FileMenuRepository(tempDir.resolve("menu-order.txt").toString());
        OrderRepository orderRepository = new FileOrderRepository(tempDir.resolve("orders.txt").toString(), tempDir.resolve("order-details.txt").toString());
        OrderStatusNotifier notifier = new OrderStatusNotifier();
        DeliveryService deliveryService = new DeliveryService(userRepository);
        PaymentService paymentService = new PaymentService(new TieredDiscountPolicy());
        OrderService orderService = new OrderService(orderRepository, paymentService, notifier, deliveryService);

        MenuItem pizza = new MenuItem(1, 1, "Pizza", 200.0, true);
        menuRepository.save(pizza);

        Cart cart = new Cart("cust-1");
        cart.addItem(pizza, 1);

        PaymentStrategy paymentStrategy = cartToPay -> new Bill(cart.getTotalAmount(), 0.0, 0.0, cart.getTotalAmount(), PaymentType.CASH);
        Order order = orderService.placeOrder("cust-1", cart, paymentStrategy);
        assertEquals(OrderStatus.PLACED, order.getStatus());

        DeliveryPerson deliveryPerson = deliveryService.addDeliveryPerson("Ravi", "ravi2@example.com", "pass", "8888888888", "KA03EF4567");
        Order accepted = orderService.acceptOrder(order.getId());
        assertEquals(OrderStatus.PREPARING, orderService.getOrderById(order.getId()).getStatus());

        Order ready = orderService.markReady(order.getId());
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, orderService.getOrderById(order.getId()).getStatus());

        Order assigned = orderService.assignDeliveryPerson(order.getId(), deliveryPerson.getId());
        assertEquals(deliveryPerson.getId(), assigned.getDeliveryPersonId());
        assertFalse(deliveryService.getDeliveryPersonById(deliveryPerson.getId()).isAvailable());

        Order delivered = orderService.advanceOrderStatus(order.getId());
        assertEquals(OrderStatus.DELIVERED, delivered.getStatus());
        assertTrue(deliveryService.getDeliveryPersonById(deliveryPerson.getId()).isAvailable());
    }
}
