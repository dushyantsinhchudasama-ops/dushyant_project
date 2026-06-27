package com.fooddelivery.model;

import com.fooddelivery.enums.PaymentType;

public class Bill {
    private final double originalAmount;
    private final double discountPercentage;
    private final double discountAmount;
    private final double finalAmount;
    private final PaymentType paymentType;

    public Bill(double originalAmount, double discountPercentage, double discountAmount, double finalAmount, PaymentType paymentType) {
        this.originalAmount = originalAmount;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.paymentType = paymentType;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "originalAmount=" + originalAmount +
                ", discountPercentage=" + discountPercentage +
                ", discountAmount=" + discountAmount +
                ", finalAmount=" + finalAmount +
                ", paymentType=" + paymentType +
                '}';
    }
}
