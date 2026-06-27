package com.fooddelivery.observer;

import com.fooddelivery.model.Order;

public class EmailNotificationObserver implements OrderObserver {
    @Override
    public void update(Order order) {
        System.out.println("[Email Notification] Order " + order.getId() + " is now " + order.getStatus() + ".");
    }
}
