package com.fooddelivery.service;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.exception.OrderNotFoundException;
import com.fooddelivery.model.Bill;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.model.DeliveryPerson;
import com.fooddelivery.observer.OrderStatusNotifier;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.state.OrderState;
import com.fooddelivery.state.OrderStateFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final OrderStatusNotifier orderStatusNotifier;
    private final DeliveryService deliveryService;

    public OrderService(OrderRepository orderRepository,
                        PaymentService paymentService,
                        OrderStatusNotifier orderStatusNotifier,
                        DeliveryService deliveryService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.orderStatusNotifier = orderStatusNotifier;
        this.deliveryService = deliveryService;
    }

    public Order placeOrder(String customerId, Cart cart, com.fooddelivery.strategy.PaymentStrategy paymentStrategy) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty.");
        }
        Bill bill = paymentService.processPayment(cart, paymentStrategy);
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> new OrderItem(
                        cartItem.getMenuItem().getId(),
                        cartItem.getMenuItem().getName(),
                        cartItem.getQuantity(),
                        cartItem.getMenuItem().getPrice()))
                .collect(Collectors.toList());

        Order order = new Order(
                generateNextOrderId(),
                customerId,
                null,
                orderItems,
                bill.getOriginalAmount(),
                bill.getDiscountPercentage(),
                bill.getDiscountAmount(),
                bill.getFinalAmount(),
                bill.getPaymentType(),
                OrderStatus.PLACED,
                LocalDateTime.now());
        orderRepository.save(order);
        orderStatusNotifier.notifyObservers(order);
        return order;
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersByDeliveryPerson(String deliveryPersonId) {
        return orderRepository.findByDeliveryPersonId(deliveryPersonId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order assignDeliveryPerson(String orderId, String deliveryPersonId) {
        Order order = getOrderById(orderId);
        if (order.getDeliveryPersonId() != null) {
            throw new IllegalStateException("Delivery person already assigned.");
        }
        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
            throw new IllegalStateException("Order must be ready for delivery before assignment. Current status: " + order.getStatus());
        }

        DeliveryPerson deliveryPerson;
        if (deliveryPersonId == null || deliveryPersonId.isBlank()) {
            deliveryPerson = deliveryService.findAvailableDeliveryPerson();
        } else {
            deliveryPerson = deliveryService.getDeliveryPersonById(deliveryPersonId);
            if (!deliveryPerson.isAvailable()) {
                throw new IllegalStateException("Delivery person is not available: " + deliveryPersonId);
            }
        }

        String assignedDeliveryPersonId = deliveryPerson.getId();
        order.setDeliveryPersonId(assignedDeliveryPersonId);
        deliveryService.updateDeliveryPerson(assignedDeliveryPersonId, deliveryPerson.getName(), deliveryPerson.getEmail(), deliveryPerson.getPhoneNumber(), deliveryPerson.getVehicleNumber(), false);
        orderRepository.update(order);
        orderStatusNotifier.notifyObservers(order);
        return order;
    }

    public Order acceptOrder(String orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException("Only placed orders can be accepted.");
        }
        order.setStatus(OrderStatus.PREPARING);
        orderRepository.update(order);
        orderStatusNotifier.notifyObservers(order);
        return order;
    }

    public Order markReady(String orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PREPARING) {
            throw new IllegalStateException("Only preparing orders can be marked ready.");
        }
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.update(order);
        orderStatusNotifier.notifyObservers(order);
        return order;
    }

    public Order rejectOrder(String orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order cannot be rejected: " + order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.update(order);
        orderStatusNotifier.notifyObservers(order);
        return order;
    }

    public Order advanceOrderStatus(String orderId) {
        Order order = getOrderById(orderId);
        OrderState currentState = OrderStateFactory.getState(order.getStatus());
        if (currentState.isFinalState()) {
            throw new IllegalStateException("Order is already final: " + order.getStatus());
        }
        OrderState nextState = currentState.nextState();
        nextState.apply(order);
        orderRepository.update(order);
        orderStatusNotifier.notifyObservers(order);
        if (order.getStatus() == OrderStatus.DELIVERED && order.getDeliveryPersonId() != null) {
            deliveryService.updateDeliveryPerson(order.getDeliveryPersonId(),
                    deliveryService.getDeliveryPersonById(order.getDeliveryPersonId()).getName(),
                    deliveryService.getDeliveryPersonById(order.getDeliveryPersonId()).getEmail(),
                    deliveryService.getDeliveryPersonById(order.getDeliveryPersonId()).getPhoneNumber(),
                    deliveryService.getDeliveryPersonById(order.getDeliveryPersonId()).getVehicleNumber(),
                    true);
        }
        return order;
    }

    public Order cancelOrder(String orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order cannot be cancelled: " + order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.update(order);
        orderStatusNotifier.notifyObservers(order);
        return order;
    }

    public void registerObserver(com.fooddelivery.observer.OrderObserver observer) {
        orderStatusNotifier.registerObserver(observer);
    }

    public void unregisterObserver(com.fooddelivery.observer.OrderObserver observer) {
        orderStatusNotifier.unregisterObserver(observer);
    }

    private String generateNextOrderId() {
        int maxId = orderRepository.findAll().stream()
                .map(Order::getId)
                .mapToInt(this::parseNumericId)
                .max()
                .orElse(0);
        return String.valueOf(maxId + 1);
    }

    private int parseNumericId(String id) {
        if (id == null || id.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
