package com.fooddelivery.service;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.model.AbstractUser;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.UserRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StatisticsService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public StatisticsService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public double getTotalRevenue() {
        return orderRepository.findAll().stream()
                .mapToDouble(Order::getFinalAmount)
                .sum();
    }

    public double getTodaysRevenue() {
        LocalDate today = LocalDate.now();
        return orderRepository.findAll().stream()
                .filter(order -> order.getOrderDate().toLocalDate().isEqual(today))
                .mapToDouble(Order::getFinalAmount)
                .sum();
    }

    public long getTotalOrders() {
        return orderRepository.findAll().size();
    }

    public long getDeliveredOrders() {
        return orderRepository.findByStatus(com.fooddelivery.enums.OrderStatus.DELIVERED).size();
    }

    public long getCancelledOrders() {
        return orderRepository.findByStatus(com.fooddelivery.enums.OrderStatus.CANCELLED).size();
    }

    public Optional<String> getMostOrderedItem() {
        Map<String, Integer> itemCounts = new HashMap<>();
        for (Order order : orderRepository.findAll()) {
            for (OrderItem item : order.getItems()) {
                itemCounts.merge(item.getItemName(), item.getQuantity(), Integer::sum);
            }
        }
        return itemCounts.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

    public Optional<String> getMostActiveCustomer() {
        Map<String, Long> customerCounts = new HashMap<>();
        for (Order order : orderRepository.findAll()) {
            customerCounts.merge(order.getCustomerId(), 1L, Long::sum);
        }
        return customerCounts.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(entry -> {
                    String customerId = entry.getKey();
                    AbstractUser user = userRepository.findById(customerId).orElse(null);
                    return user == null ? customerId : user.getName();
                });
    }
}
