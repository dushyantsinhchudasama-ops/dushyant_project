package com.fooddelivery.state;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.model.Order;

public class PreparingState implements OrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PREPARING;
    }

    @Override
    public OrderState nextState() {
        return new OutForDeliveryState();
    }

    @Override
    public void apply(Order order) {
        order.setStatus(getStatus());
    }
}
