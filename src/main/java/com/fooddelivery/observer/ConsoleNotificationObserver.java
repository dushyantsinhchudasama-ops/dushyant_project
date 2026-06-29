package com.fooddelivery.observer;

import com.fooddelivery.model.Order;

public class ConsoleNotificationObserver implements OrderObserver {
    @Override
    public void update(Order order) {
        System.out.println(" Order " + order.getId() + " status changed to " + order.getStatus());
    }
}
