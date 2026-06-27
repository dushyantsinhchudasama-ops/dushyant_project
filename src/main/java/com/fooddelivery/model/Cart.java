package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {
    private final String customerId;
    private final List<CartItem> items = new ArrayList<>();

    public Cart(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(MenuItem menuItem, int quantity) {
        CartItem existingItem = items.stream()
                .filter(item -> item.getMenuItem().getId() == menuItem.getId())
                .findFirst()
                .orElse(null);
        if (existingItem == null) {
            items.add(new CartItem(menuItem, quantity));
        } else {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        }
    }

    public void removeItem(int menuItemId) {
        items.removeIf(item -> item.getMenuItem().getId() == menuItemId);
    }

    public void updateQuantity(int menuItemId, int quantity) {
        CartItem item = items.stream()
                .filter(cartItem -> cartItem.getMenuItem().getId() == menuItemId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart: " + menuItemId));
        item.setQuantity(quantity);
    }

    public void clear() {
        items.clear();
    }

    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "customerId='" + customerId + '\'' +
                ", items=" + items +
                '}';
    }
}
