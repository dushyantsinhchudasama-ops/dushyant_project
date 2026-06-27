package com.fooddelivery.state;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.model.Order;

public class PlacedState implements OrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PLACED;
    }

    @Override
    public OrderState nextState() {
        return new PreparingState();
    }

    @Override
    public void apply(Order order) {
        order.setStatus(getStatus());
    }
}
