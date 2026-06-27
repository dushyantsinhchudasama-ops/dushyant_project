package com.fooddelivery.observer;

import com.fooddelivery.model.Order;

public class SmsNotificationObserver implements OrderObserver {
    @Override
    public void update(Order order) {
        System.out.println("[SMS Notification] Order " + order.getId() + " status updated to " + order.getStatus() + ".");
    }
}
