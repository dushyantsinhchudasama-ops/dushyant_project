package com.fooddelivery.observer;

import com.fooddelivery.model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderStatusNotifier {
    private final List<OrderObserver> observers = new ArrayList<>();

    public void registerObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Order order) {
        for (OrderObserver observer : Collections.unmodifiableList(observers)) {
            observer.update(order);
        }
    }
}
