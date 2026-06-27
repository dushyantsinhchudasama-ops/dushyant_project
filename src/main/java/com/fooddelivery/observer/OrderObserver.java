package com.fooddelivery.observer;

import com.fooddelivery.model.Order;

public interface OrderObserver {
    void update(Order order);
}
