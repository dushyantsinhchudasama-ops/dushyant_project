package com.fooddelivery.state;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.model.Order;

public class DeliveredState implements OrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.DELIVERED;
    }

    @Override
    public OrderState nextState() {
        return this;
    }

    @Override
    public void apply(Order order) {
        order.setStatus(getStatus());
    }
}
