package com.fooddelivery.strategy;

import com.fooddelivery.enums.PaymentType;
import com.fooddelivery.model.Bill;
import com.fooddelivery.model.Cart;
import com.fooddelivery.service.DiscountPolicy;

public class CardPaymentStrategy implements PaymentStrategy {
    private final DiscountPolicy discountPolicy;

    public CardPaymentStrategy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Bill pay(Cart cart) {
        double total = cart.getTotalAmount();
        double discountPercentage = discountPolicy.calculateDiscountPercentage(total);
        double discountAmount = total * discountPercentage / 100.0;
        double finalAmount = total - discountAmount;
        return new Bill(total, discountPercentage, discountAmount, finalAmount, PaymentType.CARD);
    }
}
