package com.fooddelivery.state;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.model.Order;

public interface OrderState {
    OrderStatus getStatus();
    OrderState nextState();
    void apply(Order order);
    default boolean isFinalState() {
        return getStatus() == OrderStatus.DELIVERED || getStatus() == OrderStatus.CANCELLED;
    }
}
