package com.fooddelivery.service;

import com.fooddelivery.model.Bill;
import com.fooddelivery.model.Cart;
import com.fooddelivery.strategy.PaymentStrategy;

public class PaymentService {
    private final DiscountPolicy discountPolicy;

    public PaymentService(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    public Bill processPayment(Cart cart, PaymentStrategy paymentStrategy) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty or null.");
        }
        return paymentStrategy.pay(cart);
    }

    public DiscountPolicy getDiscountPolicy() {
        return discountPolicy;
    }
}
