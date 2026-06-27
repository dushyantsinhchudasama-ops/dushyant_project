package com.fooddelivery.state;

import com.fooddelivery.enums.OrderStatus;

public final class OrderStateFactory {
    private OrderStateFactory() {
    }

    public static OrderState getState(OrderStatus status) {
        return switch (status) {
            case PLACED -> new PlacedState();
            case PREPARING -> new PreparingState();
            case OUT_FOR_DELIVERY -> new OutForDeliveryState();
            case DELIVERED -> new DeliveredState();
            case CANCELLED -> new CancelledState();
        };
    }
}
