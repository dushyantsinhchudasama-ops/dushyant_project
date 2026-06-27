package com.fooddelivery.state;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.model.Order;

public class OutForDeliveryState implements OrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.OUT_FOR_DELIVERY;
    }

    @Override
    public OrderState nextState() {
        return new DeliveredState();
    }

    @Override
    public void apply(Order order) {
        order.setStatus(getStatus());
    }
}
