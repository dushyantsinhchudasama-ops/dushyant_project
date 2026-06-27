package com.fooddelivery.repository;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    List<Order> findAll();
    Optional<Order> findById(String id);
    List<Order> findByCustomerId(String customerId);
    List<Order> findByDeliveryPersonId(String deliveryPersonId);
    List<Order> findByStatus(OrderStatus status);
    void save(Order order);
    void update(Order order);
    void deleteById(String id);
}
