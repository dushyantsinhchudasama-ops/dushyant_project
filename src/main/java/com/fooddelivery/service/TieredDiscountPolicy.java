package com.fooddelivery.service;

public class TieredDiscountPolicy implements DiscountPolicy {
    @Override
    public double calculateDiscountPercentage(double amount) {
        if (amount >= 2000) {
            return 20.0;
        }
        if (amount >= 1000) {
            return 15.0;
        }
        if (amount >= 500) {
            return 10.0;
        }
        return 0.0;
    }
}
