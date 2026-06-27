package com.fooddelivery.state;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.model.Order;

public class CancelledState implements OrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CANCELLED;
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
