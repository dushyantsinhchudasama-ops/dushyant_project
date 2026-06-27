package com.fooddelivery.strategy;

import com.fooddelivery.model.Bill;
import com.fooddelivery.model.Cart;

public interface PaymentStrategy {
    Bill pay(Cart cart);
}
